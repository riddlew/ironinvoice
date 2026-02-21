package dev.riddle.ironinvoice.features.mappings.api.dto.mapping_config.rules;

public record IntFormatRule(
	String pattern
) implements MappingRule {}
