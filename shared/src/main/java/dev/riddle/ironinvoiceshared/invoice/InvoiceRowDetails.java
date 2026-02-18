package dev.riddle.ironinvoiceshared.invoice;

import java.util.Map;

public record InvoiceRowDetails(
	int rowIndex,
	Map<String, String> data
) {}
