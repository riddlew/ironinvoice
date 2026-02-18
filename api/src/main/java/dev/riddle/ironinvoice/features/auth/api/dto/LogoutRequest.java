package dev.riddle.ironinvoice.features.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
	@NotBlank
	String refreshToken
) {}
