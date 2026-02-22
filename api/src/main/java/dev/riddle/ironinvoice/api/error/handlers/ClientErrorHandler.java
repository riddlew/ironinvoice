package dev.riddle.ironinvoice.api.error.handlers;

import dev.riddle.ironinvoice.api.error.ApiError;
import dev.riddle.ironinvoice.api.error.exceptions.ApiException;
import dev.riddle.ironinvoice.api.error.exceptions.StorageException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ClientErrorHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiError> handleApiException(ApiException ex, HttpServletRequest request) {
		ApiError body = new ApiError(
			ex.getCode(),
			ex.getMessage(),
			request.getRequestURI(),
			Instant.now()
		);

		return ResponseEntity
			.status(ex.getStatus())
			.body(body);
	}

	@ExceptionHandler(StorageException.class)
	public ResponseEntity<ApiError> handleStorageException(StorageException ex, HttpServletRequest request) {
		ApiError body = new ApiError(
			"STORAGE_ERROR",
			ex.getMessage(),
			request.getRequestURI(),
			Instant.now()
		);

		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(body);
	}
}
