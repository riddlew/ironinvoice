package dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.sources;

public record ColumnSource(
	String header
) implements MappingSource {}
