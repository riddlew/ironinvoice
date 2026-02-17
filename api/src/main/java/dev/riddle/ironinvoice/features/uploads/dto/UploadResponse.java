package dev.riddle.ironinvoice.features.uploads.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record UploadResponse(
	UUID id,
	String originalFilename,
	int rowCount,
	List<String> headers,
	List<Map<String, String>> sampleRows
) {}
