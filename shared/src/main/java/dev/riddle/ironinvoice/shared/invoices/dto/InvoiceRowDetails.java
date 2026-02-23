package dev.riddle.ironinvoice.shared.invoices.dto;

import java.util.Map;

public record InvoiceRowDetails(
	int rowIndex,
	Map<String, String> data
) {}
