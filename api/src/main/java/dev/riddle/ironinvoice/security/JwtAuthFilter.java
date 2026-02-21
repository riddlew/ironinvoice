package dev.riddle.ironinvoice.security;

import dev.riddle.ironinvoice.features.auth.application.JwtTokenService;
import dev.riddle.ironinvoice.features.users.persistence.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtTokenService jwtTokenService;
	private final UserRepository userRepository;

	public JwtAuthFilter(
		JwtTokenService jwtTokenService,
		UserRepository userRepository
	) {
		this.jwtTokenService = jwtTokenService;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = header.substring("Bearer ".length()).trim();

		jwtTokenService
			.tryBuildAuthentication(token)
			.filter(this::userExistsForAuthentication)
			.ifPresent(auth -> {
				if (SecurityContextHolder.getContext().getAuthentication() == null) {
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			});

		filterChain.doFilter(request, response);
	}

	private boolean userExistsForAuthentication(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		if (!(principal instanceof AuthUser authUser) || authUser.id() == null) {
			return false;
		}

		return userRepository.existsById(authUser.id());
	}
}
