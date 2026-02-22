package dev.riddle.ironinvoice.worker.invoices.application;

import dev.riddle.ironinvoice.shared.invoices.dto.InvoiceRowDetails;
import dev.riddle.ironinvoice.shared.invoices.persistence.InvoiceEntity;
import dev.riddle.ironinvoice.shared.invoices.persistence.InvoiceRowEntity;
import dev.riddle.ironinvoice.worker.invoices.dto.CreateInvoiceRequest;
import dev.riddle.ironinvoice.worker.invoices.persistence.InvoiceRepository;
import dev.riddle.ironinvoice.worker.invoices.persistence.InvoiceRowRepository;
import dev.riddle.ironinvoice.worker.uploads.persistence.UploadRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceService {

	private final InvoiceRepository invoiceRepository;
	private final InvoiceRowRepository invoiceRowRepository;
	private final UploadRepository uploadRepository;

	@Transactional
	public InvoiceEntity createInvoice(CreateInvoiceRequest upload) {
		InvoiceEntity invoice = new InvoiceEntity();
		invoice.setUploadId(upload.uploadId());
		invoice.setCreatedBy(upload.createdBy());
		invoice.setMappingId(upload.mappingId());
		invoice.setTemplateId(upload.templateId());

		invoiceRepository.save(invoice);

		for (InvoiceRowDetails row : upload.rows()) {
			InvoiceRowEntity invoiceRow = new InvoiceRowEntity();
			invoiceRow.setInvoice(invoice);
			invoiceRow.setRowIndex(row.rowIndex());
			invoiceRow.setData(row.data());
			invoiceRowRepository.save(invoiceRow);

			invoice.getRows().add(invoiceRow);
		}

		return invoiceRepository.save(invoice);
	}

//	@Transactional
//	public void addRowsToInvoice(UUID invoiceId, UUID userId, List<Map<String, String>> rows) {
//		InvoiceEntity invoice = invoiceRepository
//			.findByIdAndCreatedBy(invoiceId, userId)
//			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
//
//		UploadEntity upload = uploadRepository
//			.findByIdAndCreatedBy(invoice.getUploadId(), userId)
//			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Upload not found"));
//
//		if (invoiceRowRepository.countByInvoiceId(invoiceId) > 0) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice already has rows");
//		}
//
//		List<InvoiceRowEntity> invoiceRows = new ArrayList<>();
//
//		for (int i = 0; i < rows.size(); i++) {
//			InvoiceRowEntity invoiceRow = new InvoiceRowEntity();
//			invoiceRow.setInvoice(invoice);
//			invoiceRow.setRowIndex(i + 1);
//			invoiceRow.setData(rows.get(i));
//			invoiceRow.setCreatedAt(OffsetDateTime.now());
//			invoiceRow.setUpdatedAt(OffsetDateTime.now());
//			invoiceRows.add(invoiceRow);
//		}
//
//		invoiceRowRepository.saveAll(invoiceRows);
//	}
//
//	public Page<InvoiceEntity> getInvoices(UUID userId, Pageable pageable) {
//		return invoiceRepository.findByCreatedBy(userId, pageable);
//	}
//
//	public InvoiceEntity getInvoice(UUID invoiceId, UUID userId) {
//		return invoiceRepository
//			.findByIdAndCreatedBy(invoiceId, userId)
//			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
//	}
//
//	public Page<InvoiceRowEntity> getInvoiceRows(UUID invoiceId, Pageable pageable) {
//		return invoiceRowRepository.findByInvoiceId(invoiceId, pageable);
//	}
//
//	@Transactional
//	public InvoiceRowEntity patchInvoiceRow(UUID userId, UUID invoiceId, int rowIndex, Map<String, String> data) {
//		InvoiceRowEntity invoiceRow = invoiceRowRepository
//			.findByInvoiceIdAndRowIndex(invoiceId, rowIndex)
//			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice row not found"));
//
//		if (!invoiceRow.getInvoice().getCreatedBy().equals(userId)) {
//			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this invoice row");
//		}
//
//		invoiceRow.setData(data);
//		invoiceRow.setUpdatedAt(OffsetDateTime.now());
//		return invoiceRowRepository.save(invoiceRow);
//	}
//
//	// TODO: delete row (requires decrementing the rowIndex of all rows with a greater rowIndex)
//	// TODO: edit row (requires updating the rowIndex of all rows with a greater rowIndex)
//	// TODO: change row index (requires updating the rowIndex of all rows with a greater rowIndex)
}
