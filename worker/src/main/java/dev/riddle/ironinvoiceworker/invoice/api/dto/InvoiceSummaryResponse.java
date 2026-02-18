package dev.riddle.ironinvoiceworker.invoice.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InvoiceSummaryResponse(
	UUID id,
	UUID uploadId,
	UUID templateId,
	UUID mappingId,
	OffsetDateTime createdAt,
	OffsetDateTime updatedAt
) {}
