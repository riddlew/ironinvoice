package dev.riddle.ironinvoice.shared.security;

import java.util.UUID;

public record AuthUser(
	UUID id,
	String email
) {}
