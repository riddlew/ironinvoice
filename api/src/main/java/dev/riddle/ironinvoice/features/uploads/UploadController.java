package dev.riddle.ironinvoice.features.uploads;

import dev.riddle.ironinvoice.features.uploads.dto.UploadMetadata;
import dev.riddle.ironinvoice.features.uploads.dto.UploadMetadataResponse;
import dev.riddle.ironinvoice.features.uploads.dto.UploadResponse;
import dev.riddle.ironinvoice.features.uploads.dto.UploadResult;
import dev.riddle.ironinvoice.security.CurrentUser;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

	private final UploadService uploadService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UploadResponse> upload(
		@RequestPart("file") @NotNull MultipartFile file
	) {
		UUID userId = CurrentUser.requireId();
		UploadResult result = uploadService.createUpload(userId, file);

		return ResponseEntity.ok(
			new UploadResponse(
				result.id(),
				result.originalFilename(),
				result.rowCount(),
				result.headers(),
				result.sampleRows()
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
				metadata.rowCount(),
				metadata.headers(),
				metadata.createdAt()
			)
		);
	}
}
