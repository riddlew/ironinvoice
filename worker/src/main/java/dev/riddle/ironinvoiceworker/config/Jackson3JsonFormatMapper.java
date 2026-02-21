package dev.riddle.ironinvoiceworker.config;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.format.FormatMapper;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.type.TypeFactory;

/**
 * This is needed to serialize jsonb using Jackson 3, since Spring Boot 4
 * uses Jackson 3 but Hibernate expects Jackson 2.
 */
public final class Jackson3JsonFormatMapper implements FormatMapper {

	private final JsonMapper mapper;

	public Jackson3JsonFormatMapper() {
		this.mapper = JsonMapper
			.builder()
			.findAndAddModules()
			.build();
	}

	@Override
	public <T> T fromString(CharSequence charSequence, JavaType<T> javaType, WrapperOptions wrapperOptions) {
		if (charSequence == null) return null;

		try {
			TypeFactory typeFactory = mapper.getTypeFactory();

			tools.jackson.databind.JavaType jacksonType = (javaType.getJavaType() != null)
				? typeFactory.constructType(javaType.getJavaType())
				: typeFactory.constructType(javaType.getJavaTypeClass());

			return mapper.readValue(charSequence.toString(), jacksonType);

		} catch (JacksonException ex) {
			throw new IllegalArgumentException("Failed to deserialize JSON to " + javaType.getTypeName() + ": " + charSequence, ex);
		}
	}

	@Override
	public <T> String toString(T value, JavaType<T> javaType, WrapperOptions wrapperOptions) {
		if (value == null) return null;

		try {
			return mapper.writeValueAsString(value);

		} catch (JacksonException ex) {
			throw new IllegalArgumentException("Failed to serialize " + value.getClass().getName() + " to JSON", ex);
		}
	}
}