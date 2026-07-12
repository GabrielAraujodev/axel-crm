-- V3__create_pipeline_and_deals.sql
-- Sales pipelines, stages and deals.

CREATE TABLE pipelines
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_pipelines_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE TABLE pipeline_stages
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    pipeline_id     UUID NOT NULL,
    name            VARCHAR(200) NOT NULL,
    order_index     INT NOT NULL,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_pipeline_stages_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_pipeline_stages_pipeline FOREIGN KEY (pipeline_id) REFERENCES pipelines (id)
);

CREATE TABLE deals
(
    id                  UUID PRIMARY KEY,
    organization_id     UUID NOT NULL,
    lead_id             UUID,
    pipeline_id         UUID,
    stage_id            UUID,
    client_id           UUID,
    contact_id          UUID,
    assigned_to         UUID,
    title               VARCHAR(200) NOT NULL,
    description         TEXT,
    value               DECIMAL(15, 2) DEFAULT 0,
    expected_close_date DATE,
    closed_at           TIMESTAMP,
    won                 BOOLEAN,
    created_at          TIMESTAMP      DEFAULT NOW(),
    updated_at          TIMESTAMP,
    deleted_at          TIMESTAMP,
    CONSTRAINT fk_deals_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_deals_lead FOREIGN KEY (lead_id) REFERENCES leads (id),
    CONSTRAINT fk_deals_pipeline FOREIGN KEY (pipeline_id) REFERENCES pipelines (id),
    CONSTRAINT fk_deals_stage FOREIGN KEY (stage_id) REFERENCES pipeline_stages (id),
    CONSTRAINT fk_deals_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_deals_contact FOREIGN KEY (contact_id) REFERENCES contacts (id),
    CONSTRAINT fk_deals_assigned_to FOREIGN KEY (assigned_to) REFERENCES users (id)
);

CREATE INDEX idx_pipelines_organization_id ON pipelines (organization_id);

CREATE INDEX idx_pipeline_stages_organization_id ON pipeline_stages (organization_id);
CREATE INDEX idx_pipeline_stages_pipeline_id ON pipeline_stages (pipeline_id);

CREATE INDEX idx_deals_organization_id ON deals (organization_id);
CREATE INDEX idx_deals_lead_id ON deals (lead_id);
CREATE INDEX idx_deals_pipeline_id ON deals (pipeline_id);
CREATE INDEX idx_deals_stage_id ON deals (stage_id);
CREATE INDEX idx_deals_client_id ON deals (client_id);
CREATE INDEX idx_deals_assigned_to ON deals (assigned_to);
CREATE INDEX idx_deals_won ON deals (won);
CREATE INDEX idx_deals_expected_close_date ON deals (expected_close_date);
