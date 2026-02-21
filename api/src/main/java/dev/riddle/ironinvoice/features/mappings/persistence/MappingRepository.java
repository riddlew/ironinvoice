package dev.riddle.ironinvoice.features.mappings.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MappingRepository extends CrudRepository<MappingEntity, UUID> {
	Optional<MappingEntity> getMappingByIdAndCreatedBy(UUID mappingId, UUID createdBy);
}
