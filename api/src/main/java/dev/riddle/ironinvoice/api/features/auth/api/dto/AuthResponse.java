package dev.riddle.ironinvoice.api.features.auth.api.dto;

public record AuthResponse(
	String accessToken,
	String refreshToken
) {}