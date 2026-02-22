package dev.riddle.ironinvoice.worker.uploads.application.exceptions;

public class InvalidCsvException extends Exception {
	public enum Reason {
		INVALID_HEADERS,
		INVALID_FILE,
		PARSE_ERROR
	}

	public final Reason reason;

	public InvalidCsvException(Reason reason, String message) {
		super(message);
		this.reason = reason;
	}

	public Reason getReason() {
		return reason;
	}
}
