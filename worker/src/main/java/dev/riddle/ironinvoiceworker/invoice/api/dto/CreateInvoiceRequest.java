package dev.riddle.ironinvoiceworker.invoice.api.dto;

import dev.riddle.ironinvoiceshared.invoice.InvoiceRowDetails;

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
