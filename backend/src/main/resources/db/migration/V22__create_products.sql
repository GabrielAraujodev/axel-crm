-- V22__create_products.sql
-- Products / Services catalog table for use in proposals and contracts.

CREATE TABLE products
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name            VARCHAR(300) NOT NULL,
    description     TEXT,
    sku             VARCHAR(50) UNIQUE,
    category        VARCHAR(30),
    unit_price      DECIMAL(15, 2) DEFAULT 0,
    cost_price      DECIMAL(15, 2),
    unit            VARCHAR(20),
    is_active       BOOLEAN DEFAULT true,
    notes           TEXT,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_products_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE INDEX idx_products_organization ON products (organization_id);
CREATE INDEX idx_products_category ON products (category);
CREATE INDEX idx_products_sku ON products (sku);
