package dev.riddle.ironinvoice.features.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
	@NotBlank
	String refreshToken
) {}