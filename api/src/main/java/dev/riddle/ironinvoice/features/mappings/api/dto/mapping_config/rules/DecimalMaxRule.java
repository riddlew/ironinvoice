package dev.riddle.ironinvoice.features.mappings.api.dto.mapping_config.rules;

import java.math.BigDecimal;

public record DecimalMaxRule(
	BigDecimal value
) implements MappingRule {}
