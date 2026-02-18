package dev.riddle.ironinvoiceworker.upload.application;

import dev.riddle.ironinvoiceshared.uploads.contracts.CsvScan;
import dev.riddle.ironinvoiceshared.uploads.contracts.UploadJobMessage;
import dev.riddle.ironinvoiceshared.uploads.enums.UploadStatus;
import dev.riddle.ironinvoiceworker.invoice.api.dto.CreateInvoiceRequest;
import dev.riddle.ironinvoiceworker.invoice.application.InvoiceService;
import dev.riddle.ironinvoiceworker.invoice.persistence.InvoiceEntity;
import dev.riddle.ironinvoiceworker.upload.persistence.UploadEntity;
import dev.riddle.ironinvoiceworker.upload.persistence.UploadRepository;
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
	private final UploadRepository uploadRepository;

	@Transactional
	public void receiveJob(UploadJobMessage request) {
		log.info("Processing job: {}", request);

		try {
			UploadEntity upload = uploadJobService.getUpload(request.createdBy(), request.uploadId());
			upload.setStatus(UploadStatus.PROCESSING);
			uploadRepository.save(upload);

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
