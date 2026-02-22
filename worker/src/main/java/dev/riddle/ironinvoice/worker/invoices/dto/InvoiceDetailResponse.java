package dev.riddle.ironinvoice.worker.invoices.dto;

import dev.riddle.ironinvoice.shared.invoices.dto.InvoiceRowDetails;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record InvoiceDetailResponse(
	UUID id,
	UUID uploadId,
	UUID templateId,
	UUID mappingId,
	Map<String, Object> customFields,
	List<InvoiceRowDetails> rows,
	OffsetDateTime createdAt,
	OffsetDateTime updatedAt
) {}