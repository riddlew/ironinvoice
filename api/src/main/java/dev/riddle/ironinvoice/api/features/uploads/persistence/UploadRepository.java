package dev.riddle.ironinvoice.api.features.uploads.persistence;

import dev.riddle.ironinvoice.shared.uploads.persistence.UploadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UploadRepository extends JpaRepository<UploadEntity, UUID> {
	Optional<UploadEntity> findByIdAndCreatedBy(UUID id, UUID createdBy);
}
