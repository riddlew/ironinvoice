package dev.riddle.ironinvoice.shared.mappings.domain.mapping_config;

import java.util.List;

public record MappingSchema(
	List<String> requiredHeaders
) {}