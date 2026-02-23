package dev.riddle.ironinvoice.api.features.mappings.api.dto;

import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingConfig;
import jakarta.annotation.Nullable;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

public record UpdateMappingRequest(
	@Nullable
	UUID templateId,

	@Length(min = 1)
	String name,

	@Nullable
	MappingConfig config
) {}