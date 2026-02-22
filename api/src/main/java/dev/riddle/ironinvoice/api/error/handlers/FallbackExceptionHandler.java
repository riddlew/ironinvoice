package dev.riddle.ironinvoice.api.error.handlers;

import dev.riddle.ironinvoice.api.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class FallbackExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest request) {
		ApiError body = new ApiError(
			"INTERNAL_SERVER_ERROR",
			"An internal error occurred",
			request.getRequestURI(),
			Instant.now()
		);

		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(body);
	}
}
