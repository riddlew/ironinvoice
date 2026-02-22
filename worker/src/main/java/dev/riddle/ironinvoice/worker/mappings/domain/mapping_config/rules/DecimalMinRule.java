package dev.riddle.ironinvoice.worker.mappings.domain.mapping_config.rules;

import java.math.BigDecimal;

public record DecimalMinRule(
	BigDecimal value
) implements MappingRule {}
