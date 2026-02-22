package dev.riddle.ironinvoice.worker.mappings.domain.mapping_config.rules;

public record IntFormatRule(
	String pattern
) implements MappingRule {}
