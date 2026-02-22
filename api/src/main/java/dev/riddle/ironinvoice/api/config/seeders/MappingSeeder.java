package dev.riddle.ironinvoice.api.config.seeders;

import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.MappingConfig;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.MappingField;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.MappingOptions;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.rules.DateFormatRule;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.rules.DecimalMinRule;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.rules.IntMinRule;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.sources.ColumnSource;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.sources.ExprSource;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.sources.NowSource;
import dev.riddle.ironinvoice.api.features.mappings.persistence.MappingEntity;
import dev.riddle.ironinvoice.api.features.mappings.persistence.MappingRepository;
import dev.riddle.ironinvoice.api.features.users.persistence.UserEntity;
import dev.riddle.ironinvoice.api.features.users.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@Order(2)
@Profile("!production")
@RequiredArgsConstructor
public class MappingSeeder implements ApplicationRunner {

	private final UserRepository userRepository;
	private final MappingRepository mappingRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
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

		MappingEntity mapping = new MappingEntity();
		mapping.setCreatedBy(testUser.getId());
		mapping.setName("Test Mapping");
		mapping.setConfig(config);

		mappingRepository.save(mapping);
	}
}
