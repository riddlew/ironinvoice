package dev.riddle.ironinvoice.features.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
	@NotBlank
	String refreshToken
) {}
