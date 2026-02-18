package dev.riddle.ironinvoice.features.uploads.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UploadMetadataResponse(
	UUID id,
	String originalFilename,
	OffsetDateTime createdAt
) {}
