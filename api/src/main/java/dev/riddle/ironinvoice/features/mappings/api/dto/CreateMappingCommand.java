package dev.riddle.ironinvoice.features.mappings.api.dto;

import dev.riddle.ironinvoice.features.mappings.api.dto.mapping_config.MappingConfig;

import java.util.UUID;

public record CreateMappingCommand(
	UUID userId,
	UUID templateId,
	String name,
	MappingConfig config
) {}
