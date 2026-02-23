package dev.riddle.ironinvoice.api.config.seeders;

import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingConfig;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingField;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingOptions;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingSchema;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.rules.DateFormatRule;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.rules.DecimalMinRule;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.rules.IntMinRule;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.sources.ColumnSource;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.sources.ExprSource;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.sources.NowSource;
import dev.riddle.ironinvoice.api.features.mappings.persistence.MappingRepository;
import dev.riddle.ironinvoice.api.features.users.persistence.UserEntity;
import dev.riddle.ironinvoice.api.features.users.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Order(2)
@Profile("!production")
@RequiredArgsConstructor
public class MappingSeeder implements ApplicationRunner {

	private final UserRepository userRepository;
	private final MappingRepository mappingRepository;
	private final ObjectMapper objectMapper;
	private final NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("Number of mappings: " + mappingRepository.count());
		if (mappingRepository.count() > 0) return;

		System.out.println("Seeding mappings...");

		UserEntity testUser = userRepository.findAll().getFirst();

		MappingConfig config = new MappingConfig(
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
					true,
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

		MappingSchema schema = new MappingSchema(List.of(
			"Invoice #", "Customer Name", "Item Name", "Qty", "Item Description", "Item Price"
		));

		UUID id = UUID.fromString("dde81510-626f-4b74-83ce-2addc003e0ef");
		OffsetDateTime now = OffsetDateTime.now();

		String configJson = objectMapper.writeValueAsString(config);
		String schemaJson = objectMapper.writeValueAsString(schema);

		var params =new MapSqlParameterSource()
			.addValue("id", id)
			.addValue("created_by", testUser.getId())
			.addValue("name", "Test Mapping")
			.addValue("schema", schemaJson)
			.addValue("config", configJson)
			.addValue("created_at", now)
			.addValue("updated_at", now);

		jdbcTemplate.update("""
				INSERT INTO mappings (
					id, created_by, name, schema, config, created_at, updated_at
				) VALUES (
					:id, :created_by, :name, :schema::jsonb, :config::jsonb, :created_at, :updated_at
				)
			""", params);
	}
}
