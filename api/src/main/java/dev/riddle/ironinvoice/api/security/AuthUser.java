package dev.riddle.ironinvoice.api.security;

import java.util.UUID;

public record AuthUser(
	UUID id,
	String email
) {}
