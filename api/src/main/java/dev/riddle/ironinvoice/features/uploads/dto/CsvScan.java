package dev.riddle.ironinvoice.features.uploads.dto;

import java.util.List;
import java.util.Map;

public record CsvScan(
	List<String> headers,
	int rowCount,
	List<Map<String, String>> sampleRows
) {}
