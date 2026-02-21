package dev.riddle.ironinvoice.features.uploads.api.dto;

import dev.riddle.ironinvoice.features.uploads.persistence.UploadEntity;

import java.util.UUID;

public record CreateUploadJobRequest(
	UploadEntity uploadEntity,
	UUID mappingId,
	UUID templateId
) {}
