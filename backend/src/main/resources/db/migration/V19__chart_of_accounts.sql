-- V19__chart_of_accounts.sql
-- Chart of accounts hierarchical table, financial transaction linkage and multi-level commissions

CREATE TABLE chart_of_accounts
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    code            VARCHAR(50) NOT NULL,
    name            VARCHAR(200) NOT NULL,
    type            VARCHAR(50) NOT NULL, -- 'RECEITA', 'DESPESA', 'ATIVO', 'PASSIVO'
    parent_id       UUID,
    level           INT NOT NULL,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_chart_of_accounts_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_chart_of_accounts_parent FOREIGN KEY (parent_id) REFERENCES chart_of_accounts (id)
);

CREATE INDEX idx_chart_of_accounts_org_id ON chart_of_accounts (organization_id);
CREATE INDEX idx_chart_of_accounts_parent_id ON chart_of_accounts (parent_id);

-- Link financial transactions to chart of accounts
ALTER TABLE financial_transactions ADD COLUMN chart_account_id UUID REFERENCES chart_of_accounts(id);
CREATE INDEX idx_financial_transactions_chart_account_id ON financial_transactions (chart_account_id);

-- Add multi-level commission fields to proposals
ALTER TABLE proposals ADD COLUMN capture_user_id UUID REFERENCES users(id);
ALTER TABLE proposals ADD COLUMN seller_user_id UUID REFERENCES users(id);
ALTER TABLE proposals ADD COLUMN collaborator_user_id UUID REFERENCES users(id);

ALTER TABLE proposals ADD COLUMN capture_rate DECIMAL(5, 4);
ALTER TABLE proposals ADD COLUMN seller_rate DECIMAL(5, 4);
ALTER TABLE proposals ADD COLUMN partner_rate DECIMAL(5, 4);
ALTER TABLE proposals ADD COLUMN collaborator_rate DECIMAL(5, 4);

-- Expand commissions table
ALTER TABLE commissions ADD COLUMN partner_id UUID REFERENCES partners(id);
ALTER TABLE commissions ADD COLUMN role VARCHAR(50);
ALTER TABLE commissions ADD COLUMN available_at DATE;

ALTER TABLE commissions ALTER COLUMN user_id DROP NOT NULL;
ALTER TABLE commissions ALTER COLUMN rule_id DROP NOT NULL;
