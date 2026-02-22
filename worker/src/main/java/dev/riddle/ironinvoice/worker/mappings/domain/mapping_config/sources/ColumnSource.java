package dev.riddle.ironinvoice.worker.mappings.domain.mapping_config.sources;

public record ColumnSource(
	String header
) implements MappingSource {}
