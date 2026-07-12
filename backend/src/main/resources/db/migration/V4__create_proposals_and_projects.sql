-- V4__create_proposals_and_projects.sql
-- Proposals, proposal items and projects.

CREATE TABLE proposals
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    client_id       UUID,
    lead_id         UUID,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    status          VARCHAR(50)  DEFAULT 'DRAFT',
    total_value     DECIMAL(15, 2),
    valid_until     DATE,
    notes           TEXT,
    created_by      UUID,
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_proposals_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_proposals_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_proposals_lead FOREIGN KEY (lead_id) REFERENCES leads (id)
);

CREATE TABLE proposal_items
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    proposal_id     UUID NOT NULL,
    description     VARCHAR(500) NOT NULL,
    quantity        INT NOT NULL,
    unit_price      DECIMAL(15, 2) NOT NULL,
    total_price     DECIMAL(15, 2),
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_proposal_items_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_proposal_items_proposal FOREIGN KEY (proposal_id) REFERENCES proposals (id)
);

CREATE TABLE projects
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    client_id       UUID,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    status          VARCHAR(50)  DEFAULT 'PLANNED',
    start_date      DATE,
    end_date        DATE,
    budget          DECIMAL(15, 2),
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_projects_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_projects_client FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE INDEX idx_proposals_organization_id ON proposals (organization_id);
CREATE INDEX idx_proposals_client_id ON proposals (client_id);
CREATE INDEX idx_proposals_lead_id ON proposals (lead_id);
CREATE INDEX idx_proposals_status ON proposals (status);
CREATE INDEX idx_proposals_valid_until ON proposals (valid_until);

CREATE INDEX idx_proposal_items_organization_id ON proposal_items (organization_id);
CREATE INDEX idx_proposal_items_proposal_id ON proposal_items (proposal_id);

CREATE INDEX idx_projects_organization_id ON projects (organization_id);
CREATE INDEX idx_projects_client_id ON projects (client_id);
CREATE INDEX idx_projects_status ON projects (status);
CREATE INDEX idx_projects_start_date ON projects (start_date);
CREATE INDEX idx_projects_end_date ON projects (end_date);
