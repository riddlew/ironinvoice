package dev.riddle.ironinvoice.worker.uploads.application.exceptions;

public class UploadNotFoundException extends RuntimeException {
	public UploadNotFoundException(String message) {
		super(message);
	}
}
