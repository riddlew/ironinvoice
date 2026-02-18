package dev.riddle.ironinvoiceworker.invoice.api.dto;

import java.util.UUID;

public record CreateInvoiceResponse(
	UUID invoiceId
) {}
