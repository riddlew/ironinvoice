package dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.rules;

import java.util.List;

public record EnumRule(
	List<String> values
) implements MappingRule {}
