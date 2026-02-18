package dev.riddle.ironinvoiceworker.upload.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UploadRepository extends JpaRepository<UploadEntity, UUID> {
	Optional<UploadEntity> findByIdAndCreatedBy(UUID id, UUID createdBy);
}
