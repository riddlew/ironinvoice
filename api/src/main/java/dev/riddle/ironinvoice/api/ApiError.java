package dev.riddle.ironinvoice.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

public class ApiError {

	@Getter
	private final String code;

	@Getter
	private final String message;

	@Getter
	private final String path;

	@Getter
	private final Instant timestamp;

	public ApiError(String code, String message) {
		this(code, message, null, Instant.now());
	}

	public ApiError(
		String code,
		String message,
		String path,
		Instant timestamp
	) {
		this.code = code;
		this.message = message;
		this.path = path;
		this.timestamp = timestamp;
	}

}
