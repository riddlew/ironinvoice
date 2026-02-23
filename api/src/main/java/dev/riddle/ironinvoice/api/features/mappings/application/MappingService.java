package dev.riddle.ironinvoice.api.features.mappings.application;

import dev.riddle.ironinvoice.api.features.mappings.application.commands.CreateMappingCommand;
import dev.riddle.ironinvoice.api.features.mappings.application.commands.UpdateMappingCommand;
import dev.riddle.ironinvoice.api.features.mappings.persistence.MappingRepository;
import dev.riddle.ironinvoice.shared.mappings.application.exceptions.MappingNotFoundException;
import dev.riddle.ironinvoice.shared.mappings.persistence.MappingEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

	@Transactional
	public MappingEntity getMappingbyId(UUID mappingId, UUID userId) {
		return mappingRepository
			.getByIdAndCreatedBy(mappingId, userId)
			.orElseThrow(() -> new MappingNotFoundException(mappingId));
	}

	@Transactional
	public List<MappingEntity> getMappingsByUserId(UUID userId) {
		return mappingRepository.findAllByCreatedBy(userId);
	}

	@Transactional
	public MappingEntity updateMapping(UpdateMappingCommand command) {
		MappingEntity mapping = getMappingbyId(command.mappingId(), command.userId());

		mapping.setTemplateId(command.templateId());

		if (command.name() != null) {
			mapping.setName(command.name());
		}

		if (command.config() != null) {
			mapping.setConfig(command.config());
		}

		return mappingRepository.save(mapping);
	}

	@Transactional
	public void deleteMapping(UUID mappingId, UUID userId) {
		MappingEntity mapping = getMappingbyId(mappingId, userId);

		mappingRepository.delete(mapping);
	}
}
