package dev.riddle.ironinvoice.worker.mappings.application.commands;

import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingConfig;

import java.util.UUID;

public record CreateMappingCommand(
	UUID userId,
	UUID templateId,
	String name,
	MappingConfig config
) {}
