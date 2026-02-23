package dev.riddle.ironinvoice.shared.uploads.application.exceptions;

import lombok.Getter;

import java.util.UUID;

public class UploadNotFoundException extends RuntimeException {

	@Getter
	private UUID uploadId;

	public UploadNotFoundException(UUID uploadId) {
		super("Upload not found: " + uploadId + "");
		this.uploadId = uploadId;
	}
}
