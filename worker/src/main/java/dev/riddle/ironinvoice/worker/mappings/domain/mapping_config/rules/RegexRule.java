package dev.riddle.ironinvoice.worker.mappings.domain.mapping_config.rules;

public record RegexRule(
	String pattern
) implements MappingRule {}
