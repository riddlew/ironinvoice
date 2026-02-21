package dev.riddle.ironinvoice.features.mappings.api.dto.mapping_config.sources;

public record ColumnSource(
	String header
) implements MappingSource {}
