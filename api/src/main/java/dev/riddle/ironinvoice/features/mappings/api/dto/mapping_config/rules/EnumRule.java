package dev.riddle.ironinvoice.features.mappings.api.dto.mapping_config.rules;

import java.util.List;

public record EnumRule(
	List<String> values
) implements MappingRule {}
