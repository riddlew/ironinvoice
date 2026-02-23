package dev.riddle.ironinvoice.worker.mappings.persistence;

import dev.riddle.ironinvoice.shared.mappings.persistence.MappingEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MappingRepository extends CrudRepository<MappingEntity, UUID> {
	Optional<MappingEntity> getByIdAndCreatedBy(UUID mappingId, UUID createdBy);
}
