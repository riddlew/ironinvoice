package dev.riddle.ironinvoice.features.mappings.api.dto.mapping_config.sources;

public sealed interface MappingSource permits
	ColumnSource, IndexSource, ConstSource, NowSource, ExprSource {
}
