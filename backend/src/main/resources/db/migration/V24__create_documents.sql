CREATE TABLE documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    tags VARCHAR(500),
    file_name VARCHAR(255),
    file_type VARCHAR(100),
    file_size BIGINT,
    file_url VARCHAR(500),
    client_id UUID REFERENCES clients(id),
    deal_id UUID REFERENCES deals(id),
    contract_id UUID REFERENCES contracts(id),
    project_id UUID REFERENCES projects(id),
    document_date DATE,
    expiry_date DATE,
    is_archived BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP
);

CREATE INDEX idx_documents_organization ON documents(organization_id);
CREATE INDEX idx_documents_client ON documents(client_id);
CREATE INDEX idx_documents_deal ON documents(deal_id);
CREATE INDEX idx_documents_contract ON documents(contract_id);
CREATE INDEX idx_documents_project ON documents(project_id);
CREATE INDEX idx_documents_category ON documents(category);
CREATE INDEX idx_documents_archived ON documents(is_archived);
