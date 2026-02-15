package dev.riddle.ironinvoice.security;

import java.util.UUID;

public record AuthUser(
	UUID id,
	String email
) {}
