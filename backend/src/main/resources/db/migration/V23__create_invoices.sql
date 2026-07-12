-- V23__create_invoices.sql
-- Invoices table for billing and accounts receivable.

CREATE TABLE invoices
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    invoice_number  VARCHAR(50) UNIQUE,
    client_id       UUID NOT NULL,
    contract_id     UUID,
    issue_date      DATE NOT NULL,
    due_date        DATE NOT NULL,
    paid_date       DATE,
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    subtotal        DECIMAL(15, 2) DEFAULT 0,
    tax_amount      DECIMAL(15, 2),
    discount_amount DECIMAL(15, 2),
    total           DECIMAL(15, 2) DEFAULT 0,
    notes           TEXT,
    payment_method  VARCHAR(50),
    paid_amount     DECIMAL(15, 2),
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_invoices_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_invoices_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_invoices_contract FOREIGN KEY (contract_id) REFERENCES contracts (id)
);

CREATE INDEX idx_invoices_organization ON invoices (organization_id);
CREATE INDEX idx_invoices_client ON invoices (client_id);
CREATE INDEX idx_invoices_status ON invoices (status);
CREATE INDEX idx_invoices_due_date ON invoices (due_date);
