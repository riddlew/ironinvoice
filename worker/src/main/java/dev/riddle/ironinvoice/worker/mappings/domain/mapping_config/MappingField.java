package dev.riddle.ironinvoice.worker.mappings.domain.mapping_config;

import dev.riddle.ironinvoice.worker.mappings.domain.mapping_config.rules.MappingRule;
import dev.riddle.ironinvoice.worker.mappings.domain.mapping_config.sources.MappingSource;

import java.util.List;

public record MappingField(
	MappingSource source,
	MappingValueType type,
	Boolean required,
	List<MappingRule> rules
) {
	public enum MappingValueType {
		STRING,
		INT,
		DECIMAL,
		DATE,
		BOOLEAN
	}
}
