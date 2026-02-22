package dev.riddle.ironinvoice.shared.uploads.contracts;

import dev.riddle.ironinvoice.shared.invoices.InvoiceRowDetails;

import java.util.List;

public record CsvScan(
	List<String> headers,
	int rowCount,
	List<InvoiceRowDetails> rows
) {}
