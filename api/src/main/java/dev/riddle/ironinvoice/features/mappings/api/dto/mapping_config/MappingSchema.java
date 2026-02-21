package dev.riddle.ironinvoice.features.mappings.api.dto.mapping_config;

import java.util.List;

public record MappingSchema(
	List<String> requiredHeaders
) {}