package dev.riddle.ironinvoice.features.uploads.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UploadMetadata(
	UUID id,
	String originalFilename,
	List<String> headers,
	int rowCount,
	OffsetDateTime createdAt
) {}
