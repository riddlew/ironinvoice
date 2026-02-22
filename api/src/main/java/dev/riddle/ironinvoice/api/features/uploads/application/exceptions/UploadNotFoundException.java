package dev.riddle.ironinvoice.api.features.uploads.application.exceptions;

import java.util.UUID;

public class UploadNotFoundException extends RuntimeException {

	private final UUID uploadId;

	public UploadNotFoundException(UUID uploadId) {
		super("Upload not found: " + uploadId);
		this.uploadId = uploadId;
	}

	public UUID getUploadId() {
		return uploadId;
	}
}
