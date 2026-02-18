package dev.riddle.ironinvoiceshared.uploads.contracts;

import dev.riddle.ironinvoiceshared.invoice.InvoiceRowDetails;

import java.util.List;

public record CsvScan(
	List<String> headers,
	int rowCount,
	List<InvoiceRowDetails> rows
) {}
