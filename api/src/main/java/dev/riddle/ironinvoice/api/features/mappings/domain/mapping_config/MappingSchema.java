package dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config;

import java.util.List;

public record MappingSchema(
	List<String> requiredHeaders
) {}