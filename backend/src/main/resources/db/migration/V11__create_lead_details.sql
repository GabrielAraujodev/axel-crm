-- V11__create_lead_details.sql
-- Create lead notes table

CREATE TABLE lead_notes
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    lead_id         UUID NOT NULL,
    content         TEXT NOT NULL,
    created_by      UUID,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_lead_notes_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_lead_notes_lead FOREIGN KEY (lead_id) REFERENCES leads (id)
);

CREATE INDEX idx_lead_notes_lead_id ON lead_notes (lead_id);
