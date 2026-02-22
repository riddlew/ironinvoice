package dev.riddle.ironinvoice.api.features.mappings.application.commands;

import dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.MappingConfig;

import java.util.UUID;

public record CreateMappingCommand(
	UUID userId,
	UUID templateId,
	String name,
	MappingConfig config
) {}
