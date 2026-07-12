-- V20__legal_processes_and_timer.sql
-- Create legal processes table and expand projects with expert fields

CREATE TABLE legal_processes
(
    id                UUID PRIMARY KEY,
    organization_id   UUID NOT NULL,
    cnj_number        VARCHAR(50) NOT NULL,
    court             VARCHAR(200),
    distribution_date DATE,
    value             DECIMAL(15, 2),
    status            VARCHAR(100),
    description       TEXT,
    created_at        TIMESTAMP DEFAULT NOW(),
    updated_at        TIMESTAMP,
    deleted_at        TIMESTAMP,
    CONSTRAINT fk_legal_processes_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE INDEX idx_legal_processes_org_id ON legal_processes (organization_id);
CREATE INDEX idx_legal_processes_cnj ON legal_processes (cnj_number);

-- Update projects table
ALTER TABLE projects ADD COLUMN legal_process_id UUID REFERENCES legal_processes(id);
ALTER TABLE projects ADD COLUMN cnj_number VARCHAR(50);
ALTER TABLE projects ADD COLUMN expert_type VARCHAR(50);
ALTER TABLE projects ADD COLUMN payment_status VARCHAR(50);
ALTER TABLE projects ADD COLUMN delivery_deadline DATE;

CREATE INDEX idx_projects_legal_process_id ON projects (legal_process_id);
