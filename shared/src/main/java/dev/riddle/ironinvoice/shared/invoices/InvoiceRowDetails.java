package dev.riddle.ironinvoice.shared.invoices;

import java.util.Map;

public record InvoiceRowDetails(
	int rowIndex,
	Map<String, String> data
) {}
