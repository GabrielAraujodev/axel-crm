-- V6__create_financial.sql
-- Financial tables: bank accounts, transactions, commissions and time entries.

CREATE TABLE bank_accounts
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name            VARCHAR(200) NOT NULL,
    bank            VARCHAR(100),
    account_number  VARCHAR(50)  NOT NULL,
    agency          VARCHAR(20),
    balance         DECIMAL(15, 2) DEFAULT 0,
    active          BOOLEAN        DEFAULT true,
    created_at      TIMESTAMP      DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_bank_accounts_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE TABLE financial_transactions
(
    id                UUID PRIMARY KEY,
    organization_id   UUID NOT NULL,
    client_id         UUID,
    type              VARCHAR(50)  NOT NULL,
    description       VARCHAR(500),
    amount            DECIMAL(15, 2) NOT NULL,
    category          VARCHAR(100),
    transaction_date  DATE NOT NULL,
    due_date          DATE,
    paid_at           TIMESTAMP,
    paid              BOOLEAN        DEFAULT false,
    payment_method    VARCHAR(50),
    bank_account_id   UUID,
    deal_id           UUID,
    created_at        TIMESTAMP      DEFAULT NOW(),
    updated_at        TIMESTAMP,
    deleted_at        TIMESTAMP,
    CONSTRAINT fk_financial_transactions_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_financial_transactions_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_financial_transactions_bank_account FOREIGN KEY (bank_account_id) REFERENCES bank_accounts (id),
    CONSTRAINT fk_financial_transactions_deal FOREIGN KEY (deal_id) REFERENCES deals (id)
);

CREATE TABLE commission_rules
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    percentage      DECIMAL(5, 4) NOT NULL,
    min_value       DECIMAL(15, 2),
    max_value       DECIMAL(15, 2),
    active          BOOLEAN      DEFAULT true,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_commission_rules_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE TABLE commissions
(
    id               UUID PRIMARY KEY,
    organization_id  UUID NOT NULL,
    deal_id          UUID NOT NULL,
    user_id          UUID NOT NULL,
    rule_id          UUID NOT NULL,
    deal_value       DECIMAL(15, 2) NOT NULL,
    amount           DECIMAL(15, 2) NOT NULL,
    paid             BOOLEAN      DEFAULT false,
    paid_at          TIMESTAMP,
    created_at       TIMESTAMP   DEFAULT NOW(),
    updated_at       TIMESTAMP,
    deleted_at       TIMESTAMP,
    CONSTRAINT fk_commissions_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_commissions_deal FOREIGN KEY (deal_id) REFERENCES deals (id),
    CONSTRAINT fk_commissions_rule FOREIGN KEY (rule_id) REFERENCES commission_rules (id),
    CONSTRAINT fk_commissions_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE time_entries
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    user_id         UUID,
    task_id         UUID,
    project_id      UUID,
    start_time      TIMESTAMP NOT NULL,
    end_time        TIMESTAMP,
    duration_minutes INT,
    description     TEXT,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_time_entries_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_time_entries_task FOREIGN KEY (task_id) REFERENCES tasks (id),
    CONSTRAINT fk_time_entries_project FOREIGN KEY (project_id) REFERENCES projects (id)
);

CREATE INDEX idx_bank_accounts_organization_id ON bank_accounts (organization_id);

CREATE INDEX idx_financial_transactions_organization_id ON financial_transactions (organization_id);
CREATE INDEX idx_financial_transactions_client_id ON financial_transactions (client_id);
CREATE INDEX idx_financial_transactions_bank_account_id ON financial_transactions (bank_account_id);
CREATE INDEX idx_financial_transactions_type ON financial_transactions (type);
CREATE INDEX idx_financial_transactions_transaction_date ON financial_transactions (transaction_date);
CREATE INDEX idx_financial_transactions_due_date ON financial_transactions (due_date);

CREATE INDEX idx_commission_rules_organization_id ON commission_rules (organization_id);

CREATE INDEX idx_commissions_organization_id ON commissions (organization_id);
CREATE INDEX idx_commissions_deal_id ON commissions (deal_id);
CREATE INDEX idx_commissions_user_id ON commissions (user_id);
CREATE INDEX idx_commissions_rule_id ON commissions (rule_id);
CREATE INDEX idx_commissions_paid ON commissions (paid);

CREATE INDEX idx_time_entries_organization_id ON time_entries (organization_id);
CREATE INDEX idx_time_entries_user_id ON time_entries (user_id);
CREATE INDEX idx_time_entries_task_id ON time_entries (task_id);
CREATE INDEX idx_time_entries_project_id ON time_entries (project_id);
CREATE INDEX idx_time_entries_start_time ON time_entries (start_time);
