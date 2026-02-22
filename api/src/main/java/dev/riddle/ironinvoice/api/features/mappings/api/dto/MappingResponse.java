package dev.riddle.ironinvoice.api.features.mappings.api.dto;

import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingConfig;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingSchema;

import java.util.UUID;

public record MappingResponse(
	UUID id,
	UUID templateId,
	String name,
	MappingSchema schema,
	MappingConfig config
) {}
