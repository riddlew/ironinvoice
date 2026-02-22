package dev.riddle.ironinvoice.shared.mappings.domain.mapping_config;

public record MappingOptions(
	boolean trimStrings,
	boolean emptyAsNull
) {}
