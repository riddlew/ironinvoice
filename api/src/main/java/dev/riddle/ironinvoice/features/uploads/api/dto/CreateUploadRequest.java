package dev.riddle.ironinvoice.features.uploads.api.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CreateUploadRequest(
	@NotNull
	MultipartFile file,

	UUID mappingId,
	UUID templateId
) {}
