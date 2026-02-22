package dev.riddle.ironinvoice.worker.mappings.domain.mapping_config.rules;

public record DecimalFormatRule(
	String pattern
) implements MappingRule {}
