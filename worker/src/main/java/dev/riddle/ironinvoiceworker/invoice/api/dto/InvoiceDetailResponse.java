package dev.riddle.ironinvoiceworker.invoice.api.dto;

import dev.riddle.ironinvoiceshared.invoice.InvoiceRowDetails;

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