package dev.riddle.ironinvoice.api.error.handlers;

import dev.riddle.ironinvoice.api.error.ApiError;
import dev.riddle.ironinvoice.shared.mappings.application.exceptions.MappingNotFoundException;
import dev.riddle.ironinvoice.shared.uploads.application.exceptions.UploadNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NotFoundHandler {

	@ExceptionHandler(UploadNotFoundException.class)
	public ResponseEntity<ApiError> handleUploadNotFound(
		UploadNotFoundException ex,
		HttpServletRequest request
	) {
		ApiError body = new ApiError("UPLOAD_NOT_FOUND", "Upload not found");

		return ResponseEntity
			.status(HttpStatus.NOT_FOUND)
			.body(body);
	}

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
