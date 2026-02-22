package dev.riddle.ironinvoice.worker.uploads.application.exceptions;

public class UploadNotFoundException extends Exception {
	public UploadNotFoundException(String message) {
		super(message);
	}
}
