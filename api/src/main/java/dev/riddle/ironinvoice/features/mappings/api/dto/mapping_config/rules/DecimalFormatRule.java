package dev.riddle.ironinvoice.features.mappings.api.dto.mapping_config.rules;

public record DecimalFormatRule(
	String pattern
) implements MappingRule {}
