package dev.riddle.ironinvoice.api.error.handlers;

import dev.riddle.ironinvoice.api.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
@Order()
public class FallbackExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest request) {
		ApiError body = new ApiError(
			"INTERNAL_SERVER_ERROR",
			"An internal error occurred",
			request.getRequestURI(),
			Instant.now()
		);

		log.error("Internal server error", ex);

		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(body);
	}
}
