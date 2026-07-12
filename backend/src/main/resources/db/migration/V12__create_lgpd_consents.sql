-- V12__create_lgpd_consents.sql
-- Create table to store LGPD consents

CREATE TABLE lgpd_consents
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID NOT NULL,
    person_email    VARCHAR(255) NOT NULL,
    consent_type    VARCHAR(100) NOT NULL,
    granted         BOOLEAN NOT NULL DEFAULT false,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    consented_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_lgpd_consents_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE INDEX idx_lgpd_consents_organization_id ON lgpd_consents (organization_id);
CREATE INDEX idx_lgpd_consents_email ON lgpd_consents (person_email);
