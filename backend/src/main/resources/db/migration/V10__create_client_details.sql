-- V10__create_client_details.sql
-- Create client notes and attachments.

CREATE TABLE client_notes
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    client_id       UUID NOT NULL,
    user_id         UUID,
    content         TEXT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_client_notes_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_client_notes_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_client_notes_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE client_attachments
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    client_id       UUID NOT NULL,
    user_id         UUID,
    file_name       VARCHAR(255) NOT NULL,
    file_type       VARCHAR(100),
    file_size       BIGINT,
    file_data       BYTEA,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_client_attachments_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_client_attachments_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_client_attachments_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_client_notes_client_id ON client_notes (client_id);
CREATE INDEX idx_client_attachments_client_id ON client_attachments (client_id);
