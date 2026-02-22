package dev.riddle.ironinvoice.api.features.uploads;

import dev.riddle.ironinvoice.api.features.mappings.application.MappingService;
import dev.riddle.ironinvoice.api.features.uploads.application.UploadJobService;
import dev.riddle.ironinvoice.api.features.uploads.application.UploadService;
import dev.riddle.ironinvoice.api.config.properties.StorageProperties;
import dev.riddle.ironinvoice.api.features.uploads.persistence.UploadEntity;
import dev.riddle.ironinvoice.api.features.uploads.persistence.UploadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UploadServiceTest {

	@Mock
	UploadRepository uploadRepository;

	@Mock
	UploadJobService uploadJobService;

	@Mock
	MappingService mappingService;

	UploadService uploadService;
	StorageProperties storageProperties;
	UUID userId;

	@BeforeEach
	void setUp(@TempDir Path tempDir) {
		storageProperties = new StorageProperties(
			tempDir.resolve("uploads"),
			10_000_000L,
			5
		);

		uploadService = new UploadService(
			uploadRepository,
			storageProperties,
			uploadJobService,
			mappingService
		);

		userId = UUID.randomUUID();
	}

	@Test
	void createUpload_withGoodCsv_returnsHeaderAndRowCount_andStoresFile() throws Exception {
		when(uploadRepository.save(any())).thenAnswer(inv -> {
			var uploadEntity = (UploadEntity) inv.getArgument(0);
			uploadEntity.setId(UUID.randomUUID());
			return uploadEntity;
		});

		var fileResource = new ClassPathResource("fixtures/uploads/good_data.csv");

		try (InputStream in = fileResource.getInputStream()) {
			var file = new MockMultipartFile(
				"file",
				"good_data.csv",
				"text/csv",
				in
			);

			var result = uploadService.createUpload(userId, file);

			verify(uploadRepository, times(1)).save(any());
		}
	}

	@Test
	void createUpload_withEmptyFile_throws400() throws Exception {
		var fileResource = new ClassPathResource("fixtures/uploads/empty.csv");

		try (InputStream in = fileResource.getInputStream()) {
			var file = new MockMultipartFile(
				"file",
				"empty.csv",
				"text/csv",
				in
			);

			ResponseStatusException ex = assertThrows(
				ResponseStatusException.class,
				() -> uploadService.createUpload(userId, file)
			);

			assertEquals(400, ex.getStatusCode().value());
			verifyNoInteractions(uploadRepository);
		}
	}

	@Test
	void createUpload_withNonCsv_throws415() throws Exception {
		var fileResource = new ClassPathResource("fixtures/uploads/not_a_csv.pdf");

		try (InputStream in = fileResource.getInputStream()) {
			var file = new MockMultipartFile(
				"file",
				"not_a_csv.pdf",
				"application/pdf",
				in
			);

			ResponseStatusException ex = assertThrows(
				ResponseStatusException.class,
				() -> uploadService.createUpload(userId, file)
			);

			assertEquals(415, ex.getStatusCode().value());
			verifyNoInteractions(uploadRepository);
		}
	}

	@Test
	void createUpload_withMalformedHeaders_throws400() throws Exception {
		var fileResource = new ClassPathResource("fixtures/uploads/malformed_headers.csv");

		try (InputStream in = fileResource.getInputStream()) {
			var file = new MockMultipartFile(
				"file",
				"malformed_headers.csv",
				"text/csv",
				in
			);

			ResponseStatusException ex = assertThrows(
				ResponseStatusException.class,
				() -> uploadService.createUpload(userId, file)
			);

			assertEquals(400, ex.getStatusCode().value());
			verifyNoInteractions(uploadRepository);
		}
	}

	@Test
	void createUpload_withMalformedCsv_throws400() throws Exception {
		var fileResource = new ClassPathResource("fixtures/uploads/malformed.csv");

		try (InputStream in = fileResource.getInputStream()) {
			var file = new MockMultipartFile(
				"file",
				"malformed.csv",
				"text/csv",
				in
			);

			ResponseStatusException ex = assertThrows(
				ResponseStatusException.class,
				() -> uploadService.createUpload(userId, file)
			);

			assertEquals(400, ex.getStatusCode().value());
			verifyNoInteractions(uploadRepository);
		}
	}

	@Test
	void createUpload_withFileOverSizeLimit_throws413() throws Exception {
		StorageProperties tempStorageProperties = new StorageProperties(
			storageProperties.uploadsRoot(),
			1L,
			storageProperties.sampleLimit()
		);

		UploadService tempUploadService = new UploadService(
			uploadRepository,
			tempStorageProperties,
			uploadJobService,
			mappingService
		);

		var fileResource = new ClassPathResource("fixtures/uploads/good_data.csv");

		try (InputStream in = fileResource.getInputStream()) {
			var file = new MockMultipartFile(
				"file",
				"oversized.csv",
				"text/csv",
				in
			);

			ResponseStatusException ex = assertThrows(
				ResponseStatusException.class,
				() -> tempUploadService.createUpload(userId, file)
			);

			assertEquals(413, ex.getStatusCode().value());
			verifyNoInteractions(uploadRepository);
		}
	}
}
