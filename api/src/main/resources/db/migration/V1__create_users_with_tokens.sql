CREATE TABLE refresh_tokens
(
	id                   UUID        NOT NULL,
	user_id              UUID        NOT NULL,
	token_hash           VARCHAR(64) NOT NULL,
	expires_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	revoked_at           TIMESTAMP WITHOUT TIME ZONE,
	replaced_by_token_id UUID,
	created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	CONSTRAINT pk_refresh_tokens PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
	user_id UUID         NOT NULL,
	role    VARCHAR(255) NOT NULL
);

CREATE TABLE users
(
	id            UUID         NOT NULL,
	email         VARCHAR(255) NOT NULL,
	display_name  VARCHAR(255) NOT NULL,
	password_hash VARCHAR(255) NOT NULL,
	is_enabled    BOOLEAN      NOT NULL,
	CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE refresh_tokens
	ADD CONSTRAINT uc_refresh_tokens_token_hash UNIQUE (token_hash);

ALTER TABLE users
	ADD CONSTRAINT uc_users_display_name UNIQUE (display_name);

ALTER TABLE users
	ADD CONSTRAINT uc_users_email UNIQUE (email);

CREATE UNIQUE INDEX ix_refresh_tokens_hash ON refresh_tokens (token_hash);

CREATE UNIQUE INDEX ix_users_email ON users (email);

ALTER TABLE refresh_tokens
	ADD CONSTRAINT FK_REFRESH_TOKENS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX ix_refresh_tokens_user ON refresh_tokens (user_id);

ALTER TABLE user_roles
	ADD CONSTRAINT fk_user_roles_on_user_entity FOREIGN KEY (user_id) REFERENCES users (id);