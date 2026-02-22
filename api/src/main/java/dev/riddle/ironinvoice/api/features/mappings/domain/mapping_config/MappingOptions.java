package dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config;

public record MappingOptions(
	boolean trimStrings,
	boolean emptyAsNull
) {}
