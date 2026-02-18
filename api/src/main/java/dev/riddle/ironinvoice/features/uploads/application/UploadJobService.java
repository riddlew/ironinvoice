package dev.riddle.ironinvoice.features.uploads.application;

import dev.riddle.ironinvoice.features.uploads.persistence.UploadEntity;
import dev.riddle.ironinvoiceshared.uploads.contracts.UploadJobMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadJobService {

	private final RabbitTemplate rabbitTemplate;

	public void processUpload(UploadEntity upload) {
		rabbitTemplate
			.convertAndSend(
				"invoice-upload-exchange",
				"invoice.upload." + upload.getId(),
				new UploadJobMessage(
					upload.getId(),
					upload.getCreatedBy(),
					upload.getStorageKey(),
					upload.getCreatedAt()
				));
	}
}
