package dev.riddle.ironinvoice.api.features.uploads.application;

import dev.riddle.ironinvoice.api.features.uploads.api.dto.CreateUploadJobRequest;
import dev.riddle.ironinvoice.shared.uploads.contracts.UploadJobMessage;
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
