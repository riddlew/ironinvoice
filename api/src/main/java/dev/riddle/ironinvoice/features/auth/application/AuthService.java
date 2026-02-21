package dev.riddle.ironinvoice.features.auth.application;

import dev.riddle.ironinvoice.features.auth.api.dto.*;
import dev.riddle.ironinvoice.features.auth.persistence.RefreshTokenEntity;
import dev.riddle.ironinvoice.features.users.persistence.UserEntity;
import dev.riddle.ironinvoice.features.users.persistence.UserRepository;
import dev.riddle.ironinvoice.security.AuthUser;
import dev.riddle.ironinvoice.features.auth.persistence.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

	private static final SecureRandom RANDOM = new SecureRandom();

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenService jwtTokenService;

	@Value("${app.security.jwt.refresh-ttl-days:30}")
	private int refreshTtlDays;

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String email = normalizeEmail(request.email());

		if (userRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
		}

		UserEntity user = new UserEntity();
		user.setEmail(email);
		user.setDisplayName(request.displayName());
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setEnabled(true);
		user.setRoles(List.of("ROLE_USER"));

		userRepository.save(user);

		return issueTokens(user);
	}

	@Transactional
	public AuthResponse login(LoginRequest request) {
		String email = normalizeEmail(request.email());

		UserEntity user = userRepository
			.findByEmail(email)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

		if (!user.isEnabled()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is disabled");
		}

		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}

		return issueTokens(user);
	}

	@Transactional
	public AuthResponse refresh(RefreshRequest request) {
		String rawRefresh = request.refreshToken();
		String hash = sha256Hex(rawRefresh);

		RefreshTokenEntity token = refreshTokenRepository
			.findByTokenHash(hash)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

		if (!token.isActive()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is expired or revoked");
		}

		UserEntity user = token.getUser();

		if (!user.isEnabled()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is disabled");
		}

		token.setRevokedAt(OffsetDateTime.now());

		RefreshTokenEntity newToken = createRefreshToken(user);
		refreshTokenRepository.save(newToken);

		token.setReplacedByTokenId(newToken.getId());
		refreshTokenRepository.save(token);

		String accessToken = jwtTokenService.createAccessToken(
			new AuthUser(user.getId(), user.getEmail()),
			user.getRoles()
		);

		return new AuthResponse(accessToken, newToken.getRawTokenForResponse());
	}

	@Transactional
	public void logout (LogoutRequest request) {
		String hash = sha256Hex(request.refreshToken());

		refreshTokenRepository
			.findByTokenHash(hash)
			.ifPresent(token -> {
				if (token.isActive()) {
					token.setRevokedAt(OffsetDateTime.now());
					refreshTokenRepository.save(token);
				}
			});
	}

	private AuthResponse issueTokens(UserEntity user) {
		String accessToken = jwtTokenService.createAccessToken(
			new AuthUser(user.getId(), user.getEmail()),
			user.getRoles()
		);

		RefreshTokenEntity refreshToken = createRefreshToken(user);
		refreshTokenRepository.save(refreshToken);

		return new AuthResponse(
			accessToken,
			refreshToken.getRawTokenForResponse()
		);
	}

	private RefreshTokenEntity createRefreshToken(UserEntity user) {
		String raw = generateRefreshTokenString();
		String hash = sha256Hex(raw);

		RefreshTokenEntity refreshToken = new RefreshTokenEntity();
		refreshToken.setUser(user);
		refreshToken.setTokenHash(hash);
		refreshToken.setExpiresAt(OffsetDateTime.now().plusDays(refreshTtlDays));
		refreshToken.setCreatedAt(OffsetDateTime.now());
		refreshToken.setRawTokenForResponse(raw);

		return refreshToken;
	}

	private static String generateRefreshTokenString() {
		byte[] bytes = new byte[32];
		RANDOM.nextBytes(bytes);

		return Base64
			.getUrlEncoder()
			.withoutPadding()
			.encodeToString(bytes);
	}

	private static String sha256Hex(String value) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] digest = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));

			return HexFormat
				.of()
				.formatHex(digest);
		} catch (Exception ex) {
			throw new IllegalStateException("Unable to hash token", ex);
		}
	}

	private static String normalizeEmail(String email) {
		if (email == null) return null;

		return email.trim().toLowerCase();
	}
}
