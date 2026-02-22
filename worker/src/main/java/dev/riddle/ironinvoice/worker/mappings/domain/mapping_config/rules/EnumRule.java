package dev.riddle.ironinvoice.worker.mappings.domain.mapping_config.rules;

import java.util.List;

public record EnumRule(
	List<String> values
) implements MappingRule {}
