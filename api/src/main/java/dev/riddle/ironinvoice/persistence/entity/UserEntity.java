package dev.riddle.ironinvoice.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
	name = "users",
	indexes = {
		@Index(name = "ix_users_email", columnList = "email", unique = true)
	}
)
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Getter
	@Setter
	private UUID id;

	@Column(name = "email", nullable = false, unique = true)
	@Getter
	@Setter
	private String email;

	@Column(name = "display_name", nullable = false, unique = true)
	@Getter
	@Setter
	private String displayName;

	@Column(name = "password_hash", nullable = false)
	@Getter
	@Setter
	private String passwordHash;

	@Column(name = "is_enabled", nullable = false)
	@Getter
	@Setter
	private boolean isEnabled = true;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "role", nullable = false)
	@Getter
	@Setter
	private List<String> roles = new ArrayList<>();
}
