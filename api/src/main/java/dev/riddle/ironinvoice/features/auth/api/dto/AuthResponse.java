package dev.riddle.ironinvoice.features.auth.api.dto;

public record AuthResponse(
	String accessToken,
	String refreshToken
) {}