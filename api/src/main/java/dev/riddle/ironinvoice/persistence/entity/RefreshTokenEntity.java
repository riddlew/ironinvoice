package dev.riddle.ironinvoice.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
	name = "refresh_tokens",
	indexes = {
		@Index(name = "ix_refresh_tokens_hash", columnList = "token_hash", unique = true),
		@Index(name = "ix_refresh_tokens_user", columnList = "user_id")
	}
)
public class RefreshTokenEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Getter
	@Setter
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	@Getter
	@Setter
	private UserEntity user;

	@Column(name = "token_hash", nullable = false, unique = true, length = 64)
	@Getter
	@Setter
	private String tokenHash;

	@Column(name = "expires_at", nullable = false)
	@Getter
	@Setter
	private OffsetDateTime expiresAt;

	@Column(name = "revoked_at")
	@Getter
	@Setter
	private OffsetDateTime revokedAt;

	@Column(name = "replaced_by_token_id")
	@Getter
	@Setter
	private UUID replacedByTokenId;

	@Column(name = "created_at", nullable = false)
	@Getter
	@Setter
	private OffsetDateTime createdAt;

	@Transient
	@Getter
	@Setter
	private String rawTokenForResponse;

	public boolean isActive() {
		return revokedAt == null && expiresAt.isAfter(OffsetDateTime.now());
	}
}
