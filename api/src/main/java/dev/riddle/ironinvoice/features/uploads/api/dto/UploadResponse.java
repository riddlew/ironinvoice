package dev.riddle.ironinvoice.features.uploads.api.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record UploadResponse(
	UUID id,
	String originalFilename
) {}
