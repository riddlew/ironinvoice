package dev.riddle.ironinvoiceworker.invoice.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
	name = "invoices",
	indexes = {
		@Index(name = "idx_invoices_created_by", columnList = "created_by"),
		@Index(name = "idx_invoices_upload_id", columnList = "upload_id"),
	}
)
public class InvoiceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Getter
	@Setter
	private UUID id;

	@Column(name = "created_by", nullable = false)
	@Getter
	@Setter
	private UUID createdBy;

	@Column(name = "upload_id", nullable = false)
	@Getter
	@Setter
	private UUID uploadId;

	@Column(name = "template_id")
	@Getter
	@Setter
	private UUID templateId;

	@Column(name = "mapping_id")
	@Getter
	@Setter
	private UUID mappingId;

	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
	@Getter
	@Setter
	private List<InvoiceRowEntity> rows = new ArrayList<>();

	@Column(name = "created_at", nullable = false)
	@Getter
	@Setter
	private OffsetDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	@Getter
	@Setter
	private OffsetDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		OffsetDateTime now = OffsetDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = OffsetDateTime.now();
	}
}
