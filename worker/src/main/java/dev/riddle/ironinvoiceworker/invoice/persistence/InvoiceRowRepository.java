package dev.riddle.ironinvoiceworker.invoice.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRowRepository extends JpaRepository<InvoiceRowEntity, UUID> {

	Page<InvoiceRowEntity> findByInvoiceId(UUID invoiceId, Pageable pageable);
	Optional<InvoiceRowEntity> findByInvoiceIdAndRowIndex(UUID invoiceId, int rowIndex);
	long countByInvoiceId(UUID invoiceId);
	void deleteByInvoiceId(UUID invoiceId);
}
