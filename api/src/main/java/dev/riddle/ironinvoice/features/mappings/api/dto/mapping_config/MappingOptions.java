package dev.riddle.ironinvoice.features.mappings.api.dto.mapping_config;

public record MappingOptions(
	boolean trimStrings,
	boolean emptyAsNull
) {}
