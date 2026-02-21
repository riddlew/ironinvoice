package dev.riddle.ironinvoice.features.mappings.api.dto.mapping_config.rules;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "rule")
@JsonSubTypes({
	@JsonSubTypes.Type(name = "DATE_FORMAT", value = DateFormatRule.class),
	@JsonSubTypes.Type(name = "DATE_MIN", value = DateMinRule.class),
	@JsonSubTypes.Type(name = "DATE_MAX", value = DateMaxRule.class),
	@JsonSubTypes.Type(name = "DECIMAL_FORMAT", value = DecimalFormatRule.class),
	@JsonSubTypes.Type(name = "DECIMAL_MIN", value = DecimalMinRule.class),
	@JsonSubTypes.Type(name = "DECIMAL_MAX", value = DecimalMaxRule.class),
	@JsonSubTypes.Type(name = "INT_FORMAT", value = IntFormatRule.class),
	@JsonSubTypes.Type(name = "INT_MIN", value = IntMinRule.class),
	@JsonSubTypes.Type(name = "INT_MAX", value = IntMaxRule.class),
	@JsonSubTypes.Type(name = "REGEX", value = RegexRule.class),
	@JsonSubTypes.Type(name = "ENUM", value = EnumRule.class)
})
public sealed interface MappingRule permits
	// Dates
	DateFormatRule,
	DateMinRule,
	DateMaxRule,

	// Currency and decimals
	// CurrencyFormatRule, // TODO: handling currency is a pain
	DecimalFormatRule,
	DecimalMinRule,
	DecimalMaxRule,

	// Numbers
	IntFormatRule,
	IntMaxRule,
	IntMinRule,

	// Strings, enums, regex
	// TODO: min string length
	// TODO: max string length
	EnumRule,
	RegexRule {
}
