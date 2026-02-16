package dev.riddle.ironinvoice.features.uploads;

import dev.riddle.ironinvoice.config.properties.StorageProperties;
import dev.riddle.ironinvoice.features.uploads.dto.CsvScan;
import dev.riddle.ironinvoice.features.uploads.dto.UploadMetadata;
import dev.riddle.ironinvoice.features.uploads.dto.UploadResult;
import dev.riddle.ironinvoice.persistence.entity.UploadEntity;
import dev.riddle.ironinvoice.persistence.repository.UploadRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

	public UploadResult createUpload(UUID userId, MultipartFile file) {
		validateFile(file);

		String originalFilename = Optional
			.ofNullable(file.getOriginalFilename())
			.orElse("upload.csv");

		String storageKey = generateStorageKey(originalFilename);
		Path storedPath = storeFile(file, storageKey);
		CsvScan scan = scanCsv(storedPath);

		UploadEntity uploadEntity = new UploadEntity();
		uploadEntity.setCreatedBy(userId);
		uploadEntity.setOriginalFilename(originalFilename);
		uploadEntity.setStorageKey(storageKey);
		uploadEntity.setRowCount(scan.rowCount());
		uploadEntity.setHeadersJson(toJson(scan.headers()));

		UploadEntity savedUploadEntity = uploadRepository.save(uploadEntity);

		return new UploadResult(
			savedUploadEntity.getId(),
			savedUploadEntity.getOriginalFilename(),
			scan.headers(),
			savedUploadEntity.getRowCount(),
			scan.sampleRows()
		);
	}

	public UploadMetadata getUpload(UUID userId, UUID uploadId) {
		UploadEntity upload = uploadRepository
			.findByIdAndCreatedBy(uploadId, userId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Upload not found"));

		return new UploadMetadata(
			upload.getId(),
			upload.getOriginalFilename(),
			fromJsonHeaders(upload.getHeadersJson()),
			upload.getRowCount(),
			upload.getCreatedAt()
		);
	}

	private void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
		}

		if (file.getSize() > storageProperties.maxBytes()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size exceeds maximum allowed");
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

			if (!destination.startsWith(uploadsRoot.toAbsolutePath().normalize())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid storage path ");
			}

			Files.copy(file.getInputStream(), destination);
			return destination;

		} catch (IOException ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save file to storage");
		}
	}

	private CsvScan scanCsv(Path path) {
		try (
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(
					Files.newInputStream(path),
					StandardCharsets.UTF_8
				));

			CSVParser parser = CSVFormat.DEFAULT
				.builder()
					.setHeader()
				.setSkipHeaderRecord(true)
				.setTrim(true)
				.build()
				.parse(reader)
		) {
			List<String> headers = new ArrayList<>(parser.getHeaderMap().keySet());
			headers.removeIf(header -> header == null || header.isBlank());

			if (headers.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV file must include headers");
			}

			int rowCount = 0;
			List<Map<String, String>> sample = new ArrayList<>();

			for (CSVRecord record : parser) {
				rowCount++;

				if (sample.size() < storageProperties.sampleLimit()) {
					Map<String, String> row = new LinkedHashMap<>();

					for (String header : headers) {
						row.put(header, record.isMapped(header) ? record.get(header) : "");
					}

					sample.add(row);
				}
			}

			return new CsvScan(headers, rowCount, sample);
		} catch (IOException ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to parse CSV file");
		}
	}

	private String toJson(List<String> headers) {
		try {
			return objectMapper.writeValueAsString(headers);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to serialize headers");
		}
	}

	private List<String> fromJsonHeaders(String headersJson) {
		try {
			return objectMapper.readValue(headersJson, new TypeReference<>() {
			});
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to deserialize headers");
		}
	}
}
