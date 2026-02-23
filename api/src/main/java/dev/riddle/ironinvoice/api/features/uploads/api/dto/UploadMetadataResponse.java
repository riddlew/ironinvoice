package dev.riddle.ironinvoice.api.features.uploads.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UploadMetadataResponse(
	UUID id,
	String originalFilename,
	OffsetDateTime createdAt
) {}
