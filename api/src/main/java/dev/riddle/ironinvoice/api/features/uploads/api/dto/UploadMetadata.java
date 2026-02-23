package dev.riddle.ironinvoice.api.features.uploads.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UploadMetadata(
	UUID id,
	String originalFilename,
//	List<String> headers,
//	int rowCount,
	OffsetDateTime createdAt
) {}
