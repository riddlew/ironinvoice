package dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.rules;

public record IntFormatRule(
	String pattern
) implements MappingRule {}
