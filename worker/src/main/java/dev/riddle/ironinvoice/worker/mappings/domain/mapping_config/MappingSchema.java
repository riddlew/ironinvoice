package dev.riddle.ironinvoice.worker.mappings.domain.mapping_config;

import java.util.List;

public record MappingSchema(
	List<String> requiredHeaders
) {}