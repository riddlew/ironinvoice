package dev.riddle.ironinvoice.api.features.mappings.api.mapper;

import dev.riddle.ironinvoice.api.features.mappings.api.dto.MappingResponse;
import dev.riddle.ironinvoice.shared.mappings.persistence.MappingEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MappingMapper {

	public MappingResponse toResponse(MappingEntity entity) {
		return new MappingResponse(
			entity.getId(),
			entity.getTemplateId(),
			entity.getName(),
			entity.getSchema(),
			entity.getConfig()
		);
	}

	public List<MappingResponse> toResponse(List<MappingEntity> entities) {
		return entities.stream()
			.map(this::toResponse)
			.toList();
	}
}
