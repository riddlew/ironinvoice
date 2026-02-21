package dev.riddle.ironinvoice.features.uploads.api.error;

import dev.riddle.ironinvoice.api.ApiError;
import dev.riddle.ironinvoice.features.mappings.application.exceptions.MappingNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MappingExceptionHandler {

	@ExceptionHandler(MappingNotFoundException.class)
	public ResponseEntity<ApiError> handleMappingNotFound(
		MappingNotFoundException ex,
		HttpServletRequest request
	) {
		ApiError body = new ApiError("MAPPING_NOT_FOUND", "Mapping not found");

		return ResponseEntity
			.status(HttpStatus.NOT_FOUND)
			.body(body);
	}
}
