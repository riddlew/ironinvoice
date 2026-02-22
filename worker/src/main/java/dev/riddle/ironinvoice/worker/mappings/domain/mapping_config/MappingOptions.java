package dev.riddle.ironinvoice.worker.mappings.domain.mapping_config;

public record MappingOptions(
	boolean trimStrings,
	boolean emptyAsNull
) {}
