package dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.sources;

public record ColumnSource(
	String header
) implements MappingSource {}
