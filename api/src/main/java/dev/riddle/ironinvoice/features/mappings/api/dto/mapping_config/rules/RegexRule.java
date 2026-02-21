package dev.riddle.ironinvoice.features.mappings.api.dto.mapping_config.rules;

public record RegexRule(
	String pattern
) implements MappingRule {}
