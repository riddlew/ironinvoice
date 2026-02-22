package dev.riddle.ironinvoice.worker.invoices.dto;

import java.util.UUID;

public record CreateInvoiceResponse(
	UUID invoiceId
) {}
