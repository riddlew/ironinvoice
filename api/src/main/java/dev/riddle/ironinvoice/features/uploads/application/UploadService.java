package dev.riddle.ironinvoice.features.uploads.application;

import dev.riddle.ironinvoice.features.uploads.api.dto.CreateUploadJobRequest;
import dev.riddle.ironinvoiceshared.uploads.enums.UploadStatus;
import dev.riddle.ironinvoice.shared.config.properties.StorageProperties;
import dev.riddle.ironinvoice.features.uploads.api.dto.UploadMetadata;
import dev.riddle.ironinvoice.features.uploads.api.dto.UploadResult;
import dev.riddle.ironinvoice.features.uploads.persistence.UploadEntity;
import dev.riddle.ironinvoice.features.uploads.persistence.UploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UploadService {

	private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
		"text/csv",
		"application/csv",
		"application/vnd.ms-excel",
		"text/plain"
	);

	private final UploadRepository uploadRepository;
	private final ObjectMapper objectMapper;
	private final StorageProperties storageProperties;
	private final UploadJobService uploadJobService;

	public UploadResult createUpload(UUID userId, MultipartFile file) {
		return createUpload(userId, file, null, null);
	}

	public UploadResult createUpload(UUID userId, MultipartFile file, UUID mappingId, UUID templateId) {
		validateFile(file);

		String originalFilename = Optional
			.ofNullable(file.getOriginalFilename())
			.orElse("upload.csv");

		String storageKey = generateStorageKey(originalFilename);
		Path storedPath = storeFile(file, storageKey);
//		CsvScan scan = scanCsv(storedPath);

		UploadEntity uploadEntity = new UploadEntity();
		uploadEntity.setCreatedBy(userId);
		uploadEntity.setOriginalFilename(originalFilename);
		uploadEntity.setStorageKey(storageKey);
		uploadEntity.setStatus(UploadStatus.PENDING);
//		uploadEntity.setRowCount(scan.rowCount());
//		uploadEntity.setHeadersJson(scan.headers());

		if (mappingId != null) {
			// TODO: validate from db that it's a valid mapping and owned by the user. If it isn't, throw.
		}

		if (templateId != null) {
			// TODO: validate from db that it's a valid mapping and owned by the user. If it isn't, throw.
		}

		UploadEntity savedUploadEntity = uploadRepository.save(uploadEntity);

		uploadJobService.processUpload(new CreateUploadJobRequest(
			uploadEntity,
			mappingId,
			templateId
		));

		return new UploadResult(
			savedUploadEntity.getId(),
			savedUploadEntity.getOriginalFilename()
//			scan.headers()
//			savedUploadEntity.getRowCount(),
//			scan.sampleRows()
		);
	}

	public UploadMetadata getUpload(UUID userId, UUID uploadId) {
		UploadEntity upload = uploadRepository
			.findByIdAndCreatedBy(uploadId, userId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Upload not found"));

		return new UploadMetadata(
			upload.getId(),
			upload.getOriginalFilename(),
//			upload.getHeadersJson(),
//			upload.getRowCount(),
			upload.getCreatedAt()
		);
	}

	private void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
		}

		if (file.getSize() > storageProperties.maxBytes()) {
			throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE, "File size exceeds maximum allowed");
		}

		String contentType = Optional
			.ofNullable(file.getContentType())
			.orElse("");

		String name = Optional
			.ofNullable(file.getOriginalFilename())
			.orElse("");

		boolean isCsvExtension = name.toLowerCase().endsWith(".csv");
		boolean isCsvContentType = contentType.isBlank() || ALLOWED_CONTENT_TYPES.contains(contentType);

		if (!isCsvExtension || !isCsvContentType) {
			throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File type not supported");
		}
	}

	private String generateStorageKey(String originalFilename) {
		String suffix = originalFilename != null && originalFilename.toLowerCase().endsWith(".csv") ? ".csv" : "";
		return UUID.randomUUID() + suffix;
	}

	private Path storeFile(MultipartFile file, String storageKey) {
		try {
			Path uploadsRoot = storageProperties.uploadsRoot();

			Files.createDirectories(uploadsRoot);

			if (
				storageKey.contains("/") ||
				storageKey.contains("\\") ||
				storageKey.contains("..")
			) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid storage key");
			}

			Path destination = uploadsRoot
				.resolve(storageKey)
				.normalize();

			Path absoluteDestination = destination.toAbsolutePath().normalize();
			Path absoluteUploadsRoot = uploadsRoot.toAbsolutePath().normalize();

			if (!absoluteDestination.startsWith(absoluteUploadsRoot)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid storage path ");
			}

			Files.copy(file.getInputStream(), absoluteDestination);
			return absoluteDestination;

		} catch (IOException ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save file to storage");
		}
	}

}
