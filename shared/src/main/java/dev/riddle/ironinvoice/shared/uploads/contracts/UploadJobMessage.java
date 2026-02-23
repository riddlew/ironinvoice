package dev.riddle.ironinvoice.shared.uploads.contracts;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UploadJobMessage(
	UUID uploadId,
	UUID mappingId,
	UUID templateId,
	UUID createdBy,
	String storageKey,
	OffsetDateTime createdAt
) {}