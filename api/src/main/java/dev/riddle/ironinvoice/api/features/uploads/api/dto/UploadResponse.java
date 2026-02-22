package dev.riddle.ironinvoice.api.features.uploads.api.dto;

import java.util.UUID;

public record UploadResponse(
	UUID id,
	String originalFilename
) {}
