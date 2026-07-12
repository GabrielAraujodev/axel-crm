-- Migration to create the prospects table

CREATE TABLE prospects (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    company VARCHAR(255),
    source VARCHAR(50) NOT NULL,
    stage VARCHAR(50) NOT NULL,
    notes TEXT,
    converted_lead_id UUID REFERENCES leads(id),
    converted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP
);

CREATE INDEX idx_prospects_organization_id ON prospects(organization_id);
CREATE INDEX idx_prospects_stage ON prospects(stage);
