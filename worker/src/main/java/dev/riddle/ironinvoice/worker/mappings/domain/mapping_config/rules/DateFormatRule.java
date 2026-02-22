package dev.riddle.ironinvoice.worker.mappings.domain.mapping_config.rules;

public record DateFormatRule(
	String pattern
) implements MappingRule {}
