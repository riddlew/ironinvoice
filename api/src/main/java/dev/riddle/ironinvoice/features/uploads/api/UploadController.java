package dev.riddle.ironinvoice.features.uploads.api;

import dev.riddle.ironinvoice.features.uploads.api.dto.*;
import dev.riddle.ironinvoice.features.uploads.application.UploadService;
import dev.riddle.ironinvoice.security.CurrentUser;
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
	private final MappingService mappingService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UploadResponse> upload(
		CreateUploadRequest request
	) {
		UUID userId = CurrentUser.requireId();

		if (request.mappingId() != null) {
			mappingService.getMappingbyId(request.mappingId(), userId);
		}

		UploadResult result = uploadService.createUpload(userId, request.file(), request.mappingId(), request.templateId());

		return ResponseEntity.ok(
			new UploadResponse(
				result.id(),
				result.originalFilename()
			)
		);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UploadMetadataResponse> getById(
		@PathVariable("id") UUID id
	) {
		UUID userId = CurrentUser.requireId();
		UploadMetadata metadata = uploadService.getUpload(userId, id);

		return ResponseEntity.ok(
			new UploadMetadataResponse(
				metadata.id(),
				metadata.originalFilename(),
				metadata.createdAt()
			)
		);
	}
}
