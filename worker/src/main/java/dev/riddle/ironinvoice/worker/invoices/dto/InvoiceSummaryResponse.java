package dev.riddle.ironinvoice.worker.invoices.dto;

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
