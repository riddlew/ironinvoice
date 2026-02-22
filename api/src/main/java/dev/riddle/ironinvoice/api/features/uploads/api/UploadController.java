package dev.riddle.ironinvoice.api.features.uploads.api;

import dev.riddle.ironinvoice.api.features.mappings.application.commands.CreateMappingCommand;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.MappingConfig;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.MappingField;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.MappingOptions;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.rules.DateFormatRule;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.rules.DecimalMinRule;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.rules.IntMinRule;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.sources.ColumnSource;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.sources.ExprSource;
import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.sources.NowSource;
import dev.riddle.ironinvoice.api.features.mappings.application.MappingService;
import dev.riddle.ironinvoice.api.features.mappings.persistence.MappingEntity;
import dev.riddle.ironinvoice.api.features.uploads.api.dto.*;
import dev.riddle.ironinvoice.api.features.uploads.application.UploadService;
import dev.riddle.ironinvoice.api.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

	private final UploadService uploadService;
	private final MappingService mappingService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UploadResponse> upload(
		CreateUploadRequest request
	) {
		UUID userId = CurrentUser.requireId();

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

		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(config));

		MappingEntity entity = mappingService.createMapping(new CreateMappingCommand(
			userId,
			request.templateId(),
			"Test Mapping",
			config
		));

		if (request.mappingId() != null) {
			mappingService.getMappingbyId(request.mappingId(), userId);
		}

		if (request.templateId() != null) {
//			mappingService.getMappingbyId(request.mappingId(), userId);
		}

		UploadResult result = uploadService.createUpload(userId, request.file(), request.mappingId(), request.templateId());

		return ResponseEntity.ok(
			new UploadResponse(
				result.id(),
				result.originalFilename()
			)
		);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UploadMetadataResponse> getById(
		@PathVariable("id") UUID id
	) {
		UUID userId = CurrentUser.requireId();
		UploadMetadata metadata = uploadService.getUpload(userId, id);

		return ResponseEntity.ok(
			new UploadMetadataResponse(
				metadata.id(),
				metadata.originalFilename(),
				metadata.createdAt()
			)
		);
	}
}
