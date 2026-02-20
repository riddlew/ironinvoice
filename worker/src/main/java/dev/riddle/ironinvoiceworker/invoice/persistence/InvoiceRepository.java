package dev.riddle.ironinvoiceworker.invoice.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, UUID> {

	Optional<InvoiceEntity> findByIdAndCreatedBy(UUID id, UUID createdBy);
	boolean existsByIdAndCreatedBy(UUID id, UUID createdBy);
	Page<InvoiceEntity> findByCreatedBy(UUID createdBy, Pageable pageable);
}
