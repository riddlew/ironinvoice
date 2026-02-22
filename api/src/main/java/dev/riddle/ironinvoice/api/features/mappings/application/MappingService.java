package dev.riddle.ironinvoice.api.features.mappings.application;

import dev.riddle.ironinvoice.api.features.mappings.application.commands.CreateMappingCommand;
import dev.riddle.ironinvoice.api.features.mappings.application.exceptions.MappingNotFoundException;
import dev.riddle.ironinvoice.api.features.mappings.persistence.MappingEntity;
import dev.riddle.ironinvoice.api.features.mappings.persistence.MappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MappingService {

	private final MappingRepository mappingRepository;

	public MappingEntity createMapping(CreateMappingCommand command) {
		MappingEntity mapping = new MappingEntity();
		mapping.setCreatedBy(command.userId());
		mapping.setTemplateId(command.templateId());
		mapping.setName(command.name());
		mapping.setConfig(command.config());

		return mappingRepository.save(mapping);
	}

	public MappingEntity getMappingbyId(UUID mappingId, UUID userId) {
		return mappingRepository
			.getMappingByIdAndCreatedBy(mappingId, userId)
			.orElseThrow(() -> new MappingNotFoundException(mappingId));
	}
}
