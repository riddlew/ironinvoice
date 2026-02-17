CREATE TABLE uploads
(
	id                UUID         NOT NULL,
	created_by        UUID         NOT NULL,
	original_filename VARCHAR(255) NOT NULL,
	storage_key       VARCHAR(255) NOT NULL,
	row_count         INTEGER      NOT NULL,
	headers_json      JSONB        NOT NULL,
	created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	CONSTRAINT pk_uploads PRIMARY KEY (id)
);

ALTER TABLE uploads
	ADD CONSTRAINT uc_uploads_storage_key UNIQUE (storage_key);

CREATE INDEX idx_uploads_created_at ON uploads (created_at);

CREATE INDEX idx_uploads_created_by ON uploads (created_by);