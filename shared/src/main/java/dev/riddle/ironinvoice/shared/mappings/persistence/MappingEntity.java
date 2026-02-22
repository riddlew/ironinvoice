package dev.riddle.ironinvoice.shared.mappings.persistence;

import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingConfig;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.MappingSchema;
import dev.riddle.ironinvoice.shared.mappings.domain.mapping_config.sources.ColumnSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
	name = "mappings",
	indexes = {
		@Index(name = "idx_uploads_created_by", columnList = "created_by"),
		@Index(name = "idx_uploads_created_at", columnList = "created_at")
	}
)
public class MappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Getter
	@Setter
	private UUID id;

	@Column(name = "template_id")
	@Getter
	@Setter
	private UUID templateId;

	@Column(name = "created_by", nullable = false)
	@Getter
	@Setter
	private UUID createdBy;

	@Column(name = "name", nullable = false)
	@Getter
	@Setter
	private String name;

	// Computed on save
	@Column(name = "schema", nullable = false, columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	@Getter
	private MappingSchema schema;

	/**
	 * Data:
	 * {
	 *  "headers": {
	 *      "invoiceNumber": {
	 *          "source": {
	 *              "type": "CONST",
	 *              "value": "AUTO"
	 *          }
	 *      },
	 *      "invoiceDate": {
	 *          "source": {
	 *              "type": "NOW"
	 *          },
	 *          "type": "date",
	 *          "format": "yyy-MM-dd"
	 *      },
	 *      "customerName": {
	 *          "source": {
	 *              "type": "COLUMN",
	 *              "header": "Customer Name"
	 *          },
	 *          "type": "string",
	 *          "required": true
	 *      },
	 *  },
	 *  "data": {
	 *      "item": {
	 *          "source": {
	 *              "type": "COLUMN",
	 *              "header": "Item"
	 *          },
	 *          "type": "string",
	 *          "required": true
	 *      },
	 *      "quantity": {
	 *          "source": {
	 *              "type": "COLUMN",
	 *              "header": "Qty"
	 *          },
	 *          "type": "int",
	 *          "required": true,
	 *          "min": 1
	 *      },
	 *      "description": {
	 *          "source": {
	 *              "type": "COLUMN",
	 *              "header": "Unit Price"
	 *          },
	 *          "type": "decimal",
	 *          "required": true
	 *      },
	 *      "lineTotal": {
	 *          "source": {
	 *              "type": "EXPR",
	 *              "expr": "quantity * unitPrice"
	 *          },
	 *          "type": "decimal"
	 *      },
	 *  },
	 *  "options": {
	 *      "trimStrings": true,
	 *      "emptyAsNull": true
	 *  }
	 * }
	 */
	@Column(name = "config", nullable = false, columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	@Getter
	@Setter
	private MappingConfig config;

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

		schema = new MappingSchema(
			generateRequiredHeaders()
		);
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = OffsetDateTime.now();

		schema = new MappingSchema(
			generateRequiredHeaders()
		);
	}

	private List<String> generateRequiredHeaders() {
		List<String> headers = new ArrayList<>();

		// Get data headers
		headers.addAll(config
			.dataFields()
			.values()
			.stream()
			.filter(field -> field.required() && (field.source() instanceof ColumnSource))
			.map(field -> ((ColumnSource) field.source()).header())
			.toList());

		// Get line headers
		headers.addAll(config
			.lineFields()
			.values()
			.stream()
			.filter(field -> field.required() && (field.source() instanceof ColumnSource))
			.map(field -> ((ColumnSource) field.source()).header())
			.toList());

		return headers;
	}
}
