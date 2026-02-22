package dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.rules;

import java.math.BigDecimal;

public record DecimalMaxRule(
	BigDecimal value
) implements MappingRule {}
