package dev.riddle.ironinvoice.api.features.uploads.api;

import dev.riddle.ironinvoice.api.features.uploads.api.dto.*;
import dev.riddle.ironinvoice.api.features.uploads.api.mapper.UploadMapper;
import dev.riddle.ironinvoice.api.features.uploads.application.UploadService;
import dev.riddle.ironinvoice.api.security.CurrentUser;
import dev.riddle.ironinvoice.shared.uploads.persistence.UploadEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

	private final UploadService uploadService;
	private final UploadMapper mapper;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UploadResponse> upload(
		CreateUploadRequest request
	) {
		UUID userId = CurrentUser.requireId();

		UploadEntity upload = uploadService.createUpload(userId, request.file(), request.mappingId(), request.templateId());

		return ResponseEntity
			.accepted()
			.body(mapper.toResponse(upload));
	}

	@GetMapping("/{id}")
	public ResponseEntity<UploadMetadataResponse> getById(
		@PathVariable("id") UUID id
	) {
		UUID userId = CurrentUser.requireId();
		UploadEntity upload = uploadService.getUpload(userId, id);

		return ResponseEntity.ok(mapper.toMetadataResponse(upload));
	}
}
