package dev.riddle.ironinvoice.api.features.mappings;

import dev.riddle.ironinvoice.api.config.properties.StorageProperties;
import dev.riddle.ironinvoice.api.features.mappings.application.MappingService;
import dev.riddle.ironinvoice.api.features.mappings.application.commands.CreateMappingCommand;
import dev.riddle.ironinvoice.api.features.mappings.persistence.MappingRepository;
import dev.riddle.ironinvoice.api.features.uploads.application.UploadJobService;
import dev.riddle.ironinvoice.api.features.uploads.application.UploadService;
import dev.riddle.ironinvoice.api.features.uploads.persistence.UploadRepository;
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
import dev.riddle.ironinvoice.shared.uploads.persistence.UploadEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MappingServiceTest {

	@Mock
	MappingRepository mappingRepository;

	MappingService mappingService;
	UUID userId;

	@BeforeEach
	void setUp(@TempDir Path tempDir) {
		mappingService = new MappingService(
			mappingRepository
		);

		userId = UUID.randomUUID();
	}

	@Test
	void createMapping_withValidData_returnsMappingEntity_andCreatesEntity() {
		when(mappingRepository.save(any())).thenAnswer(inv -> {
			var mappingEntity = (MappingEntity) inv.getArgument(0);
			mappingEntity.setId(UUID.randomUUID());
			return mappingEntity;
		});

		var result = mappingService.createMapping(new CreateMappingCommand(
			userId,
			null,
			"Test Template",
			createTestMappingConfig()
		));

		assertEquals("Test Template", result.getName());
		assertNull(result.getTemplateId());
		assertEquals(userId, result.getCreatedBy());
		assertTrue(result.getConfig().options().emptyAsNull());
		assertEquals(
			MappingField.MappingValueType.STRING,
			result.getConfig().dataFields().get("invoiceNumber").type());

		verify(mappingRepository, times(1)).save(any());
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
	}
}
