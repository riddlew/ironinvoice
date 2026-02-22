package dev.riddle.ironinvoice.api.features.mappings.application.exceptions;

import java.util.UUID;

public class MappingNotFoundException extends RuntimeException {

	private final UUID mappingId;

	public MappingNotFoundException(UUID mappingId) {
		super("Mapping not found: " + mappingId);
		this.mappingId = mappingId;
	}

	public UUID getMappingId() {
		return mappingId;
	}
}
