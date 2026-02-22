package dev.riddle.ironinvoice.api.features.mappings.api.dto;

import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingConfig;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

public record CreateMappingRequest(
	@Nullable
	UUID templateId,

	@Length(min = 1)
	String name,

	@NotNull
	MappingConfig config
) {}