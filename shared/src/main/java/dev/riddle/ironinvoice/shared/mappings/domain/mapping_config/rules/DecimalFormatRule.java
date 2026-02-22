package dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.rules;

public record DecimalFormatRule(
	String pattern
) implements MappingRule {}
