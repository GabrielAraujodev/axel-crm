-- V2__create_leads_and_clients.sql
-- Leads, clients and contacts tables.

CREATE TABLE leads
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name            VARCHAR(200) NOT NULL,
    email           VARCHAR(255),
    phone           VARCHAR(20),
    company         VARCHAR(200),
    position        VARCHAR(100),
    source          VARCHAR(50),
    stage           VARCHAR(50)  DEFAULT 'NEW',
    estimated_value DECIMAL(15, 2),
    notes           TEXT,
    assigned_to     UUID,
    converted       BOOLEAN      DEFAULT false,
    converted_at    TIMESTAMP,
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_leads_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE TABLE clients
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name            VARCHAR(200) NOT NULL,
    document        VARCHAR(50),
    email           VARCHAR(255),
    phone           VARCHAR(50),
    company_name    VARCHAR(200),
    website         VARCHAR(255),
    address         TEXT,
    city            VARCHAR(100),
    state           VARCHAR(100),
    zip_code        VARCHAR(50),
    country         VARCHAR(100),
    industry        VARCHAR(255),
    notes           TEXT,
    active          BOOLEAN      DEFAULT true,
    assigned_to     UUID,
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_clients_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_clients_assigned_to FOREIGN KEY (assigned_to) REFERENCES users (id)
);

CREATE TABLE contacts
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    client_id       UUID NOT NULL,
    name            VARCHAR(200) NOT NULL,
    email           VARCHAR(255),
    phone           VARCHAR(20),
    position        VARCHAR(100),
    is_primary      BOOLEAN      DEFAULT false,
    notes           TEXT,
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_contacts_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_contacts_client FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE INDEX idx_leads_organization_id ON leads (organization_id);
CREATE INDEX idx_leads_assigned_to ON leads (assigned_to);
CREATE INDEX idx_leads_status ON leads (stage);
CREATE INDEX idx_leads_source ON leads (source);
CREATE INDEX idx_leads_created_at ON leads (created_at);

CREATE INDEX idx_clients_organization_id ON clients (organization_id);
CREATE INDEX idx_clients_email ON clients (email);
CREATE INDEX idx_clients_active ON clients (active);

CREATE INDEX idx_contacts_organization_id ON contacts (organization_id);
CREATE INDEX idx_contacts_client_id ON contacts (client_id);
