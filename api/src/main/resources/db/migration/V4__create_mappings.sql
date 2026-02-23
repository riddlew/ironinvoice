CREATE TABLE mappings
(
	id          UUID         NOT NULL,
	template_id UUID,
	created_by  UUID         NOT NULL,
	name        VARCHAR(255) NOT NULL,
	schema      JSONB        NOT NULL,
	config      JSONB        NOT NULL,
	created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	CONSTRAINT pk_mappings PRIMARY KEY (id)
);

CREATE INDEX idx_mappings_created_at ON mappings (created_at);

CREATE INDEX idx_mappings_created_by ON mappings (created_by);