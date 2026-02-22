package dev.riddle.ironinvoice.api.features.uploads;

import dev.riddle.ironinvoice.api.config.properties.StorageProperties;
import dev.riddle.ironinvoice.api.features.users.persistence.UserEntity;
import dev.riddle.ironinvoice.api.features.users.persistence.UserRepository;
import dev.riddle.ironinvoice.api.security.AuthUser;
import dev.riddle.ironinvoice.api.features.auth.application.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
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

	@Autowired
	private UserRepository userRepository;

	@Autowired
	JwtTokenService jwtTokenService;

	StorageProperties storageProperties;
	UserEntity user;

	private String bearerFor(UserEntity userEntity) {
		String token = jwtTokenService.createAccessToken(
			new AuthUser(userEntity.getId(), userEntity.getEmail()),
			userEntity.getRoles()
		);

		return "Bearer " + token;
	}

	@BeforeEach
	void setUp(@TempDir Path tempDir) {
		storageProperties = new StorageProperties(
			tempDir.resolve("uploads"),
			10_000_000L,
			5
		);

		UUID userId = UUID.randomUUID();

		UserEntity userEntity = new UserEntity();
		userEntity.setEmail("test-" + userId + "@example.com");
		userEntity.setDisplayName("Test User " + userId);
		userEntity.setPasswordHash("user-password-hash");
		userEntity.setEnabled(true);
		userEntity.setRoles(List.of("ROLE_USER"));
		user = userRepository.save(userEntity);
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
						.header("X-Test-UserId", user.getId().toString())
						.header(HttpHeaders.AUTHORIZATION, bearerFor(user)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.rowCount", is(100)))
				.andExpect(jsonPath("$.headers", contains("name", "age", "phone_number", "email")));
		}
	}

	@Test
	void upload_withNonExistentUser_returns404() throws Exception {
		var fileResource = new ClassPathResource("fixtures/uploads/good_data.csv");
		UUID nonExistentUserId = UUID.randomUUID();

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
						.header("X-Test-UserId", user.getId().toString()))
				.andExpect(status().isForbidden());
		}
	}

	@Test
	void upload_missingFile_returns400() throws Exception {
		mockMvc.perform(
				multipart("/api/uploads")
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.header("X-Test-UserId", user.getId().toString())
					.header(HttpHeaders.AUTHORIZATION, bearerFor(user)))
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
						.header("X-Test-UserId", user.getId().toString())
						.header(HttpHeaders.AUTHORIZATION, bearerFor(user)))
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
						.header("X-Test-UserId", user.getId().toString())
						.header(HttpHeaders.AUTHORIZATION, bearerFor(user)))
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
						.header("X-Test-UserId", user.getId().toString())
						.header(HttpHeaders.AUTHORIZATION, bearerFor(user)))
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
						.header("X-Test-UserId", user.getId().toString())
						.header(HttpHeaders.AUTHORIZATION, bearerFor(user)))
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
					.header("X-Test-UserId", user.getId().toString())
					.header(HttpHeaders.AUTHORIZATION, bearerFor(user)))
			.andExpect(status().isContentTooLarge());
	}

	@Test
	void get_upload_withWrongOwner_returns404() throws Exception {
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
						.header("X-Test-UserId", user.getId().toString())
						.header(HttpHeaders.AUTHORIZATION, bearerFor(user)))
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
					.header("X-Test-UserId", user.getId().toString())
					.header(HttpHeaders.AUTHORIZATION, bearerFor(user)))
			.andExpect(status().isOk());

		mockMvc.perform(
			get("/api/uploads/{id}", id)
				.header("X-Test-UserId", secondUser.toString()))
			.andExpect(status().isForbidden());
	}
}
