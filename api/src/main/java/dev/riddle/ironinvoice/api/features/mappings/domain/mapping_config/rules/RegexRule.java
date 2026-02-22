package dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.rules;

public record RegexRule(
	String pattern
) implements MappingRule {}
