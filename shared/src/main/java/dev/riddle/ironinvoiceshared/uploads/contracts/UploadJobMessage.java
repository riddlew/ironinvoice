package dev.riddle.ironinvoiceshared.uploads.contracts;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UploadJobMessage(
	UUID uploadId,
	UUID mappingId,
	UUID templateId,
	UUID createdBy,
	String storageKey,
	OffsetDateTime createdAt
) {}