package dev.riddle.ironinvoice.api.features.uploads.api.dto;

import dev.riddle.ironinvoice.shared.uploads.persistence.UploadEntity;

import java.util.UUID;

public record CreateUploadJobRequest(
	UploadEntity uploadEntity,
	UUID mappingId,
	UUID templateId
) {}
