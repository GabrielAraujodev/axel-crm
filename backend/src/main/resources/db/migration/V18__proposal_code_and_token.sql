-- Migration to add proposal code, public token and project linkage

ALTER TABLE proposals ADD COLUMN proposal_code VARCHAR(50);
ALTER TABLE proposals ADD COLUMN public_token UUID UNIQUE;

ALTER TABLE projects ADD COLUMN source_proposal_id UUID REFERENCES proposals(id);
CREATE INDEX idx_projects_source_proposal_id ON projects(source_proposal_id);
