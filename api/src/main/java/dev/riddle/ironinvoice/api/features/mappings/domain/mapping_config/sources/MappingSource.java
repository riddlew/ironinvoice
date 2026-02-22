package dev.riddle.ironinvoice.api.features.mappings.domain.mapping_config.sources;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "source")
@JsonSubTypes({
	@JsonSubTypes.Type(name = "COLUMN", value = ColumnSource.class),
	@JsonSubTypes.Type(name = "INDEX", value = IndexSource.class),
	@JsonSubTypes.Type(name = "CONST", value = ConstSource.class),
	@JsonSubTypes.Type(name = "NOW", value = NowSource.class),
	@JsonSubTypes.Type(name = "EXPR", value = ExprSource.class)
})
public sealed interface MappingSource permits
	ColumnSource, IndexSource, ConstSource, NowSource, ExprSource {
}
