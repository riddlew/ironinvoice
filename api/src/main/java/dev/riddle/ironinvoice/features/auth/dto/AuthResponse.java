package dev.riddle.ironinvoice.features.auth.dto;

public record AuthResponse(
	String accessToken,
	String refreshToken
) {}