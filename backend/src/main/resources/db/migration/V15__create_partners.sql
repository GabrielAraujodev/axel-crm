-- Migration to create the partners table and link it to leads and proposals

CREATE TABLE partners (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    company VARCHAR(255),
    bank_details TEXT,
    commission_percentage DECIMAL(5,2) DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP
);

CREATE INDEX idx_partners_organization_id ON partners(organization_id);

ALTER TABLE leads ADD COLUMN partner_id UUID REFERENCES partners(id);
CREATE INDEX idx_leads_partner_id ON leads(partner_id);

ALTER TABLE proposals ADD COLUMN partner_id UUID REFERENCES partners(id);
CREATE INDEX idx_proposals_partner_id ON proposals(partner_id);
