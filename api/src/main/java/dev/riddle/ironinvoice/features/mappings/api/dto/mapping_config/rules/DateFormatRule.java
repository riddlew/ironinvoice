package dev.riddle.ironinvoice.features.mappings.api.dto.mapping_config.rules;

public record DateFormatRule(
	String pattern
) implements MappingRule {}
