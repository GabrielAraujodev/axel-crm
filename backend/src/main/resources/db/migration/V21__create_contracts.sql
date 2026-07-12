-- V21__create_contracts.sql
-- Contracts table for managing formal agreements with clients.

CREATE TABLE contracts
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    title           VARCHAR(300) NOT NULL,
    contract_number VARCHAR(50) UNIQUE,
    description     TEXT,
    client_id       UUID NOT NULL,
    deal_id         UUID,
    start_date      DATE NOT NULL,
    end_date        DATE,
    value           DECIMAL(15, 2) DEFAULT 0,
    monthly_value   DECIMAL(15, 2),
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    terms           TEXT,
    notes           TEXT,
    signed_by_client VARCHAR(200),
    signed_at       TIMESTAMP,
    renewed_at      TIMESTAMP,
    auto_renew      BOOLEAN DEFAULT false,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_contracts_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_contracts_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_contracts_deal FOREIGN KEY (deal_id) REFERENCES deals (id)
);

CREATE INDEX idx_contracts_organization ON contracts (organization_id);
CREATE INDEX idx_contracts_client ON contracts (client_id);
CREATE INDEX idx_contracts_status ON contracts (status);
CREATE INDEX idx_contracts_contract_number ON contracts (contract_number);
