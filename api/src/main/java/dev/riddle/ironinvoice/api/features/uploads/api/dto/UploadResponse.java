package dev.riddle.ironinvoice.api.features.uploads.api.dto;

import dev.riddle.ironinvoice.shared.uploads.enums.UploadStatus;

import java.util.UUID;

public record UploadResponse(
	UUID uploadId,
	UploadStatus status
) {}
