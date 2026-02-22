package dev.riddle.ironinvoice.api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;


/// This is a convenience wrapper around `SecurityContextHolder`.
///
/// `CurrentUser` handles grabbing `Authentication` from the context, checking
/// for null and non-authentication, casting principal, and handling edge cases.
///
/// By calling `CurrentUser.requireId()`, it gives us access to the authenticated
/// user's ID.
public class CurrentUser {

	private CurrentUser() {}

	public static Optional<AuthUser> getAuthUser() {
		Authentication auth = SecurityContextHolder
			.getContext()
			.getAuthentication();

		if (auth == null || !auth.isAuthenticated()) {
			return Optional.empty();
		}

		Object principal = auth.getPrincipal();

		if (principal instanceof AuthUser au) {
			return Optional.of(au);
		}

		if (principal instanceof String s) {
			try {
				return Optional.of(new AuthUser(UUID.fromString(s), null));
			} catch (IllegalArgumentException ex) {
				return Optional.empty();
			}
		}

		return Optional.empty();
	}

	public static UUID requireId() {
		return getAuthUser()
			.map(AuthUser::id)
			.orElseThrow(() -> new IllegalStateException("No auth user in SecurityContext"));
	}

	public static Optional<UUID> getId() {
		return getAuthUser().map(AuthUser::id);
	}

	public static Optional<String> getEmail() {
		return getAuthUser().map(AuthUser::email);
	}
}
