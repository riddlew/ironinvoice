package dev.riddle.ironinvoice.api.features.uploads.api.dto;

import java.util.UUID;

public record UploadResult(
	UUID id,
	String originalFilename
//	List<String> headers,
//	int rowCount,
//	List<Map<String, String>> sampleRows
) {}
