package dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.rules;

public record IntFormatRule(
	String pattern
) implements MappingRule {}
