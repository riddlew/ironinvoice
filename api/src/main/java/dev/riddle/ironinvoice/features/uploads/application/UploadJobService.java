package dev.riddle.ironinvoice.features.uploads.application;

import dev.riddle.ironinvoice.features.uploads.api.dto.CreateUploadJobRequest;
import dev.riddle.ironinvoiceshared.uploads.contracts.UploadJobMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadJobService {

	private final RabbitTemplate rabbitTemplate;

	public void processUpload(CreateUploadJobRequest request) {
		rabbitTemplate
			.convertAndSend(
				"invoice-upload-exchange",
				"invoice.upload." + request.uploadEntity().getId(),
				new UploadJobMessage(
					request.uploadEntity().getId(),
					request.mappingId(),
					request.templateId(),
					request.uploadEntity().getCreatedBy(),
					request.uploadEntity().getStorageKey(),
					request.uploadEntity().getCreatedAt()
				));
	}
}
