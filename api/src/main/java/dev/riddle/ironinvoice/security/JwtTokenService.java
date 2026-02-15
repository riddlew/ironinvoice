package dev.riddle.ironinvoice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class JwtTokenService {

	private final SecretKey key;
	private final String issuer;
	private final Duration accessTtl;
	private final JwtParser parser;

	public JwtTokenService(
		@Value("${app.security.jwt.secret}") String secret,
		@Value("${app.security.jwt.issuer:ironinvoice}") String issuer,
		@Value("${app.security.jwt.access-ttl-minutes:60}") long accessTtlMinutes
	) {
		this.key = Keys.hmacShaKeyFor(decodeSecret(secret));
		this.issuer = issuer;
		this.accessTtl = Duration.ofMinutes(accessTtlMinutes);
		this.parser = Jwts
			.parserBuilder()
			.requireIssuer(issuer)
			.setSigningKey(key)
			.build();
	}

	public String createAccessToken(AuthUser user, Collection<String> roles) {
		Instant now = Instant.now();
		Instant expiration = now.plus(accessTtl);

		return Jwts
			.builder()
			.setIssuer(issuer)
			.setSubject(user.id().toString())
			.setIssuedAt(Date.from(now))
			.setExpiration(Date.from(expiration))
			.claim("email", user.email())
			.claim("roles", new ArrayList<>(roles))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();

	}

	public Optional<Authentication> tryBuildAuthentication(String token) {
		try {
			Claims claims = parser.parseClaimsJws(token).getBody();
			UUID userId = UUID.fromString(claims.getSubject());
			String email = claims.get("email", String.class);
			List<String> roles = extractRoles(claims.get("roles"));

			var authorities = roles
				.stream()
				.map(SimpleGrantedAuthority::new)
				.toList();

			AuthUser principal = new AuthUser(userId, email);
			return Optional.of(new UsernamePasswordAuthenticationToken(
				principal,
				null,
				authorities
			));
		} catch (JwtException | IllegalArgumentException ex) {
			return Optional.empty();
		}
	}

	private static List<String> extractRoles(Object rolesClaim) {
		return switch (rolesClaim) {
			case Collection<?> c -> c
				.stream()
				.map(String::valueOf)
				.toList();
			// Comma-separated string fallback
			case String s -> Arrays
				.stream(s.split(","))
				.map(String::trim)
				.filter(x -> !x.isBlank())
				.toList();
			case null, default -> List.of();
		};

	}

	private static byte[] decodeSecret(String secret) {
		try {
			return Decoders.BASE64.decode(secret);
		} catch (IllegalArgumentException ex) {
			return secret.getBytes(StandardCharsets.UTF_8);
		}
	}
}
