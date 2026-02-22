package dev.riddle.ironinvoice.worker.mappings.domain.mapping_config.rules;

public record IntMaxRule(
	int value
) implements MappingRule {}
