-- V9__update_leads_schema.sql
-- Add missing columns and correct mismatches across tables to match entity models

-- 1. Table: leads
ALTER TABLE leads ADD COLUMN converted_client_id UUID;
ALTER TABLE leads ADD COLUMN score INTEGER DEFAULT 0;
ALTER TABLE leads ADD COLUMN last_contact_at TIMESTAMP;
ALTER TABLE leads ADD CONSTRAINT fk_leads_converted_client FOREIGN KEY (converted_client_id) REFERENCES clients (id);

-- 2. Table: pipelines
ALTER TABLE pipelines ADD COLUMN active BOOLEAN DEFAULT true;

-- 3. Table: pipeline_stages
ALTER TABLE pipeline_stages ADD COLUMN win_probability INTEGER DEFAULT 0;
ALTER TABLE pipeline_stages ADD COLUMN description TEXT;

-- 4. Table: proposals
ALTER TABLE proposals ADD COLUMN issue_date DATE;
ALTER TABLE proposals ADD COLUMN total_amount DECIMAL(15, 2) DEFAULT 0;
ALTER TABLE proposals ADD COLUMN discount_amount DECIMAL(15, 2) DEFAULT 0;
ALTER TABLE proposals ADD COLUMN approved_at TIMESTAMP;
ALTER TABLE proposals ADD COLUMN assigned_to UUID;
ALTER TABLE proposals ADD CONSTRAINT fk_proposals_assigned_to FOREIGN KEY (assigned_to) REFERENCES users (id);

-- 5. Table: proposal_items
ALTER TABLE proposal_items ADD COLUMN discount_amount DECIMAL(15, 2) DEFAULT 0;
ALTER TABLE proposal_items ADD COLUMN total DECIMAL(15, 2) DEFAULT 0;

-- 6. Table: projects
ALTER TABLE projects ADD COLUMN name VARCHAR(200);
-- Set a default name for safety, copy from title if available, then make it NOT NULL
UPDATE projects SET name = COALESCE(title, 'Projeto Sem Nome');
ALTER TABLE projects ALTER COLUMN name SET NOT NULL;
ALTER TABLE projects ADD COLUMN cost DECIMAL(15, 2) DEFAULT 0;
ALTER TABLE projects ADD COLUMN manager_id UUID;
ALTER TABLE projects ADD CONSTRAINT fk_projects_manager FOREIGN KEY (manager_id) REFERENCES users (id);

-- 7. Table: tasks
ALTER TABLE tasks ADD COLUMN completed_at TIMESTAMP;
ALTER TABLE tasks ADD COLUMN lead_id UUID;
ALTER TABLE tasks ADD COLUMN client_id UUID;
ALTER TABLE tasks ADD COLUMN deal_id UUID;
ALTER TABLE tasks ADD CONSTRAINT fk_tasks_lead FOREIGN KEY (lead_id) REFERENCES leads (id);
ALTER TABLE tasks ADD CONSTRAINT fk_tasks_client FOREIGN KEY (client_id) REFERENCES clients (id);
ALTER TABLE tasks ADD CONSTRAINT fk_tasks_deal FOREIGN KEY (deal_id) REFERENCES deals (id);

-- 8. Table: time_entries
ALTER TABLE time_entries ADD COLUMN hourly_rate DECIMAL(15, 2);
