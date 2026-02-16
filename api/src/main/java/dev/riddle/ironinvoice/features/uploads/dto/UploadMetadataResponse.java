package dev.riddle.ironinvoice.features.uploads.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UploadMetadataResponse(
	UUID id,
	String originalFilename,
	int rowCount,
	List<String> headers,
	OffsetDateTime createdAt
) {}
