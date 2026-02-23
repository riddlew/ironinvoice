package dev.riddle.ironinvoice.api.features.mappings;

import dev.riddle.ironinvoice.api.config.properties.StorageProperties;
import dev.riddle.ironinvoice.api.features.auth.application.JwtTokenService;
import dev.riddle.ironinvoice.api.features.mappings.api.dto.CreateMappingRequest;
import dev.riddle.ironinvoice.api.features.mappings.api.dto.MappingResponse;
import dev.riddle.ironinvoice.api.features.mappings.api.dto.UpdateMappingRequest;
import dev.riddle.ironinvoice.api.features.mappings.persistence.MappingRepository;
import dev.riddle.ironinvoice.api.features.users.persistence.UserEntity;
import dev.riddle.ironinvoice.api.features.users.persistence.UserRepository;
import dev.riddle.ironinvoice.api.security.AuthUser;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingConfig;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingField;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingOptions;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.rules.DateFormatRule;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.rules.DecimalMinRule;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.rules.IntMinRule;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.sources.ColumnSource;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.sources.ExprSource;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.sources.NowSource;
import dev.riddle.ironinvoice.shared.mappings.persistence.MappingEntity;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class MappingControllerIT {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MappingRepository mappingRepository;

	@Autowired
	JwtTokenService jwtTokenService;

	@Autowired
	ObjectMapper mapper;

	UserEntity user;

	private String bearerFor(UserEntity userEntity) {
		String token = jwtTokenService.createAccessToken(
			new AuthUser(userEntity.getId(), userEntity.getEmail()),
			userEntity.getRoles()
		);

		return "Bearer " + token;
	}

	@BeforeEach
	void setUp() {
		UUID userId = UUID.randomUUID();

		UserEntity userEntity = new UserEntity();
		userEntity.setEmail("test-" + userId + "@example.com");
		userEntity.setDisplayName("Test User " + userId);
		userEntity.setPasswordHash("user-password-hash");
		userEntity.setEnabled(true);
		userEntity.setRoles(List.of("ROLE_USER"));
		user = userRepository.save(userEntity);

		MappingConfig seedingMappingConfig = createTestMappingConfig();
		MappingEntity seedingMapping = new MappingEntity();
		seedingMapping.setName("Seeding Mapping");
		seedingMapping.setCreatedBy(user.getId());
		seedingMapping.setConfig(seedingMappingConfig);
		mappingRepository.save(seedingMapping);
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry propertyRegistry) {
		propertyRegistry.add("spring.datasource.url", postgres::getJdbcUrl);
		propertyRegistry.add("spring.datasource.username", postgres::getUsername);
		propertyRegistry.add("spring.datasource.password", postgres::getPassword);
		propertyRegistry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
	}

	@Container
	static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18")
		.withDatabaseName("ironinvoice_test")
		.withUsername("test")
		.withPassword("test");

	@Test
	void createMapping_withValidData_returns200_withMappingResponse() throws Exception {
		MappingConfig mappingConfig = createTestMappingConfig();

		CreateMappingRequest testRequest = new CreateMappingRequest(
			null,
			"Create Mapping Test",
			mappingConfig
		);

		MvcResult result = mockMvc.perform(
			post("/api/mappings")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(testRequest))
				.contentType(MediaType.APPLICATION_JSON)
				.header("X-Test-UserId", user.getId().toString())
				.header(HttpHeaders.AUTHORIZATION, bearerFor(user)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id", notNullValue()))
			.andExpect(jsonPath("$.name", is("Create Mapping Test")))
			.andExpect(jsonPath("$.schema.requiredHeaders", containsInAnyOrder(
				"Customer Name",
				"Item Description",
				"Item Price",
				"Qty",
				"Item Name")))
			.andReturn();

		String contentJson = result.getResponse().getContentAsString();
		MappingResponse response = mapper.readValue(contentJson, new TypeReference<>(){});

		assertEquals(mappingConfig, response.config());
	}

	@Test
	void patchmapping_withValidData_returns200_withMappingResponse() throws Exception {
		MappingEntity seedingMapping = mappingRepository.findAllByCreatedBy(user.getId()).getFirst();

		MappingConfig testConfig = new MappingConfig(
			Map.ofEntries(
				Map.entry("invoiceNumber", new MappingField(
					new ColumnSource("Invoice #"),
					MappingField.MappingValueType.STRING,
					false,
					List.of()
				))
			),
			Map.ofEntries(
				Map.entry("item", new MappingField(
					new ColumnSource("Item Name"),
					MappingField.MappingValueType.STRING,
					true,
					List.of()
				))
			),
			new MappingOptions(false, false)
		);

		UpdateMappingRequest testRequest = new UpdateMappingRequest(
			null,
			"Patch Test",
			testConfig
		);

		MvcResult result = mockMvc.perform(
				patch("/api/mappings/{id}", seedingMapping.getId())
					.accept(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(testRequest))
					.contentType(MediaType.APPLICATION_JSON)
					.header("X-Test-UserId", user.getId().toString())
					.header(HttpHeaders.AUTHORIZATION, bearerFor(user)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is("Patch Test")))
			.andExpect(jsonPath("$.schema.requiredHeaders", containsInAnyOrder(
				"Item Name")))
			.andReturn();

		String contentJson = result.getResponse().getContentAsString();
		MappingResponse response = mapper.readValue(contentJson, new TypeReference<>(){});

		assertEquals(testConfig, response.config());
	}

	private MappingConfig createTestMappingConfig() {
		return new MappingConfig(
			Map.ofEntries(
				Map.entry("invoiceDate", new MappingField(
					new NowSource(),
					MappingField.MappingValueType.DATE,
					true,
					List.of(
						new DateFormatRule("yyyy-MM-dd")
					)
				)),
				Map.entry("invoiceNumber", new MappingField(
					new ColumnSource("Invoice #"),
					MappingField.MappingValueType.STRING,
					false,
					List.of()
				)),
				Map.entry("customerName", new MappingField(
					new ColumnSource("Customer Name"),
					MappingField.MappingValueType.STRING,
					true,
					List.of()
				))
			),
			Map.ofEntries(
				Map.entry("item", new MappingField(
					new ColumnSource("Item Name"),
					MappingField.MappingValueType.STRING,
					true,
					List.of()
				)),
				Map.entry("quantity", new MappingField(
					new ColumnSource("Qty"),
					MappingField.MappingValueType.INT,
					true,
					List.of(
						new IntMinRule(0)
					)
				)),
				Map.entry("description", new MappingField(
					new ColumnSource("Item Description"),
					MappingField.MappingValueType.STRING,
					true,
					List.of()
				)),
				Map.entry("price", new MappingField(
					new ColumnSource("Item Price"),
					MappingField.MappingValueType.DECIMAL,
					true,
					List.of(
						new DecimalMinRule(BigDecimal.ZERO)
					)
				)),
				Map.entry("lineTotal", new MappingField(
					new ExprSource("quantity * price"),
					MappingField.MappingValueType.INT,
					true,
					List.of()
				))
			),
			new MappingOptions(true, true)
		);
	}
}
