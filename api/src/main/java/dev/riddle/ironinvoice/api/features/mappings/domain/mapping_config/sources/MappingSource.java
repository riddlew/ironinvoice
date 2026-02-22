package dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.sources;

public sealed interface MappingSource permits
	ColumnSource, IndexSource, ConstSource, NowSource, ExprSource {
}
