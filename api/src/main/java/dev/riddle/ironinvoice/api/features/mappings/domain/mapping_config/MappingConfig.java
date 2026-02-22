package dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config;

import java.util.Map;

public record MappingConfig(
	Map<String, MappingField> dataFields,
	Map<String, MappingField> lineFields,
	MappingOptions options
) {}