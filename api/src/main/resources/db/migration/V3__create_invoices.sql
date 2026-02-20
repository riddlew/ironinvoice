CREATE TABLE invoices
(
	id            UUID NOT NULL,
	created_by    UUID NOT NULL,
	upload_id     UUID NOT NULL,
	template_id   UUID,
	mapping_id    UUID,
	created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	CONSTRAINT pk_invoices PRIMARY KEY (id)
);

CREATE INDEX idx_invoices_created_by ON invoices (created_by);
CREATE INDEX idx_invoices_upload_id ON invoices (upload_id);

CREATE TABLE invoice_rows
(
	id         UUID    NOT NULL,
	invoice_id UUID    NOT NULL,
	row_index  INTEGER NOT NULL,
	data       JSONB   NOT NULL,
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	CONSTRAINT pk_invoice_rows PRIMARY KEY (id),
	CONSTRAINT fk_invoice_rows_invoice FOREIGN KEY (invoice_id)
		REFERENCES invoices (id)
);

ALTER TABLE invoice_rows
	ADD CONSTRAINT uc_invoice_rows_invoice_row_id UNIQUE (invoice_id, row_index);

