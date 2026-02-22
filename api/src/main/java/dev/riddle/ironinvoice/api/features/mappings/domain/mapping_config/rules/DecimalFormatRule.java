package dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.rules;

public record DecimalFormatRule(
	String pattern
) implements MappingRule {}
