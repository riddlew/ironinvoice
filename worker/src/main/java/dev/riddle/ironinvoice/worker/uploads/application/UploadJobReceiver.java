package dev.riddle.ironinvoice.worker.uploads.application;

import dev.riddle.ironinvoice.shared.uploads.contracts.CsvScan;
import dev.riddle.ironinvoice.shared.uploads.contracts.UploadJobMessage;
import dev.riddle.ironinvoice.shared.uploads.enums.UploadStatus;
import dev.riddle.ironinvoice.worker.invoices.dto.CreateInvoiceRequest;
import dev.riddle.ironinvoice.worker.invoices.application.InvoiceService;
import dev.riddle.ironinvoice.worker.invoices.persistence.InvoiceEntity;
import dev.riddle.ironinvoice.worker.mappings.application.MappingService;
import dev.riddle.ironinvoice.worker.uploads.persistence.UploadEntity;
import dev.riddle.ironinvoice.worker.uploads.persistence.UploadRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UploadJobReceiver {

	private final UploadJobService uploadJobService;
	private final InvoiceService invoiceService;
	private final MappingService mappingService;
	private final UploadRepository uploadRepository;

	@Transactional
	public void receiveJob(UploadJobMessage request) {
		log.info("Processing job: {}", request);

		try {
			UploadEntity upload = uploadJobService.getUpload(request.createdBy(), request.uploadId());
			upload.setStatus(UploadStatus.PROCESSING);
			uploadRepository.save(upload);

			if (request.mappingId() != null) {
				mappingService.getMappingbyId(request.mappingId(), upload.getCreatedBy());
			}

			if (request.templateId() != null) {
				// TODO: validate from db that it's a valid mapping and owned by the user. If it isn't, throw.
			}

			CsvScan data = uploadJobService.parseCsv(upload.getStorageKey());

			InvoiceEntity result = invoiceService.createInvoice(new CreateInvoiceRequest(
				upload.getId(),
				request.createdBy(),
				null,
				null,
				upload.getOriginalFilename(),
				data.rows()
			));

			log.info("Invoice created: {}", result);

			upload.setStatus(UploadStatus.DONE);
			uploadRepository.save(upload);

		} catch (Exception ex) {
			log.error("Failed to process job: {}", request, ex);
		}
	}
}
