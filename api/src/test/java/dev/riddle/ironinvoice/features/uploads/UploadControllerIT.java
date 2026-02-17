package dev.riddle.ironinvoice.features.uploads;

import dev.riddle.ironinvoice.config.properties.StorageProperties;
import dev.riddle.ironinvoice.security.AuthUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class UploadControllerIT {

	@Autowired
	MockMvc mockMvc;

	@TempDir
	static Path tempDir;

	StorageProperties storageProperties;
	UUID userId;

	@BeforeEach
	void setUp(@TempDir Path tempDir) {
		storageProperties = new StorageProperties(
			tempDir.resolve("uploads"),
			10_000_000L,
			5
		);

		userId = UUID.randomUUID();
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry propertyRegistry) {
		propertyRegistry.add("spring.datasource.url", postgres::getJdbcUrl);
		propertyRegistry.add("spring.datasource.username", postgres::getUsername);
		propertyRegistry.add("spring.datasource.password", postgres::getPassword);
		propertyRegistry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
		propertyRegistry.add("app.storage.uploads-root", () -> tempDir.resolve("uploads").toString());
	}

	@Container
	static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18")
		.withDatabaseName("ironinvoice_test")
		.withUsername("test")
		.withPassword("test");

	@TestConfiguration
	static class TestSecurityConfig {
		@Bean
		SecurityFilterChain testChain(HttpSecurity http) throws Exception {
			return http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(a -> a.anyRequest().permitAll())
				.addFilterBefore(new TestUserFilter(), UsernamePasswordAuthenticationFilter.class)
				.build();
		}
	}

	static class TestUserFilter extends OncePerRequestFilter {
		@Override
		protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
		) throws ServletException, IOException {
			try {
				String userIdHeader = request.getHeader("X-Test-UserId");
				UUID userId = userIdHeader != null ? UUID.fromString(userIdHeader) : UUID.fromString("00000000-0000-0000-0000-000000000001");
				var principal = new AuthUser(userId, "test@example.com");
				var auth = new UsernamePasswordAuthenticationToken(
					principal,
					null,
					List.of(new SimpleGrantedAuthority("ROLE_USER"))
				);

				SecurityContextHolder
					.getContext()
					.setAuthentication(auth);

				filterChain.doFilter(request, response);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			} finally {
				SecurityContextHolder.clearContext();
			}
		}
	}

	@Test
	void upload_withGoodCsv_returns200_withHeadersAndRowCount() throws Exception {
		var fileResource = new ClassPathResource("fixtures/uploads/good_data.csv");

		try (InputStream in = fileResource.getInputStream()) {
			var file = new MockMultipartFile(
				"file",
				"good_data.csv",
				"text/csv",
				in
			);

			mockMvc.perform(
					multipart("/api/uploads")
						.file(file)
						.contentType(MediaType.MULTIPART_FORM_DATA)
						.header("X-Test-UserId", userId.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.rowCount", is(100)))
				.andExpect(jsonPath("$.headers", contains("name", "age", "phone_number", "email")));
		}
	}

	@Test
	void upload_missingFile_returns400() throws Exception {
		mockMvc.perform(
				multipart("/api/uploads")
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.header("X-Test-UserId", userId.toString()))
			.andExpect(status().isBadRequest());
	}

	@Test
	void upload_emptyFile_returns400() throws Exception {
		var fileResource = new ClassPathResource("fixtures/uploads/empty.csv");

		try (InputStream in = fileResource.getInputStream()) {
			var file = new MockMultipartFile(
				"file",
				"empty.csv",
				"text/csv",
				in
			);

			mockMvc
				.perform(
					multipart("/api/uploads")
						.file(file)
						.contentType(MediaType.MULTIPART_FORM_DATA)
						.header("X-Test-UserId", userId.toString()))
				.andExpect(status().isBadRequest());
		}
	}

	@Test
	void upload_typeNotCsv_returns415() throws Exception {
		var fileResource = new ClassPathResource("fixtures/uploads/not_a_csv.pdf");

		try (InputStream in = fileResource.getInputStream()) {
			var file = new MockMultipartFile(
				"file",
				"not_a_csv.pdf",
				"application/pdf",
				in
			);

			mockMvc
				.perform(
					multipart("/api/uploads")
						.file(file)
						.contentType(MediaType.MULTIPART_FORM_DATA)
						.header("X-Test-UserId", userId.toString()))
				.andExpect(status().isUnsupportedMediaType());
		}
	}

	@Test
	void upload_withMalformedHeaders_returns400() throws Exception {
		var fileResource = new ClassPathResource("fixtures/uploads/malformed_headers.csv");

		try (InputStream in = fileResource.getInputStream()) {
			var file = new MockMultipartFile(
				"file",
				"malformed_headers.csv",
				"text/csv",
				in
			);

			mockMvc
				.perform(
					multipart("/api/uploads")
						.file(file)
						.contentType(MediaType.MULTIPART_FORM_DATA)
						.header("X-Test-UserId", userId.toString()))
				.andExpect(status().isBadRequest());
		}
	}

	@Test
	void upload_withMalformedCsv_returns400() throws Exception {
		var fileResource = new ClassPathResource("fixtures/uploads/malformed.csv");

		try (InputStream in = fileResource.getInputStream()) {
			var file = new MockMultipartFile(
				"file",
				"malformed.csv",
				"text/csv",
				in
			);

			mockMvc
				.perform(
					multipart("/api/uploads")
						.file(file)
						.contentType(MediaType.MULTIPART_FORM_DATA)
						.header("X-Test-UserId", userId.toString()))
				.andExpect(status().isBadRequest());
		}
	}

	@Test
	void upload_withTooLargeCsv_returns413() throws Exception {
		byte[] bytes = new byte[((int) storageProperties.maxBytes()) + 1];

		// set up one header so it doesn't fail for being empty
		bytes[0] = 'e';
		bytes[1] = 'm';
		bytes[2] = 'a';
		bytes[3] = 'i';
		bytes[4] = 'l';
		bytes[5] = '\n';

		var file = new MockMultipartFile(
			"file",
			"large_data.csv",
			"text/csv",
			bytes
		);

		mockMvc
			.perform(
				multipart("/api/uploads")
					.file(file)
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.header("X-Test-UserId", userId))
			.andExpect(status().isContentTooLarge());
	}

	@Test
	void get_upload_withWrongOwner_returns404() throws Exception {
		var firstUser = UUID.randomUUID();
		var secondUser = UUID.randomUUID();
		var fileResource = new ClassPathResource("fixtures/uploads/good_data.csv");

		String response;

		try (InputStream in = fileResource.getInputStream()) {
			var file = new MockMultipartFile(
				"file",
				"good_data.csv",
				"text/csv",
				in
			);

			response = mockMvc
				.perform(
					multipart("/api/uploads")
						.file(file)
						.contentType(MediaType.MULTIPART_FORM_DATA)
						.header("X-Test-UserId", firstUser))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		}

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(response);
		String id = root.get("id").asText();

		mockMvc.perform(
				get("/api/uploads/{id}", id)
					.header("X-Test-UserId", firstUser.toString()))
			.andExpect(status().isOk());

		mockMvc.perform(
			get("/api/uploads/{id}", id)
				.header("X-Test-UserId", secondUser.toString()))
			.andExpect(status().isNotFound());
	}
}
