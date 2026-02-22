package dev.riddle.ironinvoice.worker.invoices.dto;

import dev.riddle.ironinvoice.shared.invoices.InvoiceRowDetails;

import java.util.List;
import java.util.UUID;

public record CreateInvoiceRequest(
	UUID uploadId,
	UUID createdBy,
	UUID templateId,
	UUID mappingId,
	String name,
	List<InvoiceRowDetails> rows
) {}
