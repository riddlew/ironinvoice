package dev.riddle.ironinvoice.features.uploads.persistence;

import dev.riddle.ironinvoiceshared.uploads.enums.UploadStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
	name = "uploads",
	indexes = {
		@Index(name = "idx_uploads_created_by", columnList = "created_by"),
		@Index(name = "idx_uploads_created_at", columnList = "created_at")
	}
)
public class UploadEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Getter
	@Setter
	private UUID id;

	@Column(name = "created_by", nullable = false)
	@Getter
	@Setter
	private UUID createdBy;

	@Column(name = "original_filename", nullable = false)
	@Getter
	@Setter
	private String originalFilename;

	@Column(name = "storage_key", nullable = false, unique = true)
	@Getter
	@Setter
	private String storageKey;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private UploadStatus status;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false)
	@Getter
	@Setter
	private OffsetDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	@Getter
	@Setter
	private OffsetDateTime updatedAt;

	@PrePersist
	void onCreate() {
		OffsetDateTime now = OffsetDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		this.updatedAt = OffsetDateTime.now();
	}
}
