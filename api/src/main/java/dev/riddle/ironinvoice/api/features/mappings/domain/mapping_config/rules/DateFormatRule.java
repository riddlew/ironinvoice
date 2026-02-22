package dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.rules;

public record DateFormatRule(
	String pattern
) implements MappingRule {}
