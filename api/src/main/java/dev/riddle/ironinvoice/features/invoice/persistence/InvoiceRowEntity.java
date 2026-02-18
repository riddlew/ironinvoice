package dev.riddle.ironinvoice.features.invoice.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
	name = "invoice_rows",
	uniqueConstraints = @UniqueConstraint(columnNames = {
		"invoice_id", "row_index"
	})
)
public class InvoiceRowEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Getter
	@Setter
	private UUID id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_id", nullable = false)
	@Getter
	@Setter
	private InvoiceEntity invoice;

	@Column(name = "row_index", nullable = false)
	@Getter
	@Setter
	private int rowIndex;

	@Column(name = "data", nullable = false, columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	@Getter
	@Setter
	private Map<String, String> data;

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
