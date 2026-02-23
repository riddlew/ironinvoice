package dev.riddle.ironinvoice.api.features.uploads.api.mapper;

import dev.riddle.ironinvoice.api.features.uploads.api.dto.UploadMetadataResponse;
import dev.riddle.ironinvoice.api.features.uploads.api.dto.UploadResponse;
import dev.riddle.ironinvoice.shared.uploads.persistence.UploadEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UploadMapper {

	public UploadResponse toResponse(UploadEntity uploadEntity) {
		return new UploadResponse(
			uploadEntity.getId(),
			uploadEntity.getStatus()
		);
	}

	public UploadMetadataResponse toMetadataResponse(UploadEntity uploadEntity) {
		return new UploadMetadataResponse(
			uploadEntity.getId(),
			uploadEntity.getOriginalFilename(),
			uploadEntity.getCreatedAt()
		);
	}
}
