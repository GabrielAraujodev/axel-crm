-- V8__create_notifications_and_support.sql
-- Notifications, support tickets, integrations and audit logs.

CREATE TABLE notifications
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    user_id         UUID NOT NULL,
    title           VARCHAR(200) NOT NULL,
    message         TEXT,
    entity_type     VARCHAR(100),
    entity_id       VARCHAR(100),
    read_at         TIMESTAMP,
    is_read         BOOLEAN      DEFAULT false,
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_notifications_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE support_tickets
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    client_id       UUID,
    subject         VARCHAR(200) NOT NULL,
    description     TEXT,
    status          VARCHAR(50)  DEFAULT 'OPEN',
    priority        VARCHAR(20)  DEFAULT 'MEDIUM',
    assigned_to     UUID,
    created_by      UUID,
    resolved_at     TIMESTAMP,
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_support_tickets_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_support_tickets_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_support_tickets_assigned_to FOREIGN KEY (assigned_to) REFERENCES users (id),
    CONSTRAINT fk_support_tickets_created_by FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE TABLE integrations
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name            VARCHAR(200) NOT NULL,
    provider        VARCHAR(100) NOT NULL,
    credentials     TEXT,
    webhook_url     VARCHAR(255),
    api_key         VARCHAR(255),
    active          BOOLEAN      DEFAULT false,
    last_sync_at    TIMESTAMP,
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_integrations_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE TABLE audit_logs
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    user_id         UUID,
    action          VARCHAR(100) NOT NULL,
    entity_type     VARCHAR(100) NOT NULL,
    entity_id       VARCHAR(100),
    old_values      TEXT,
    new_values      TEXT,
    created_at      TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_audit_logs_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_notifications_organization_id ON notifications (organization_id);
CREATE INDEX idx_notifications_user_id ON notifications (user_id);
CREATE INDEX idx_notifications_type ON notifications (entity_type);
CREATE INDEX idx_notifications_read ON notifications (is_read);
CREATE INDEX idx_notifications_created_at ON notifications (created_at);

CREATE INDEX idx_support_tickets_organization_id ON support_tickets (organization_id);
CREATE INDEX idx_support_tickets_client_id ON support_tickets (client_id);
CREATE INDEX idx_support_tickets_status ON support_tickets (status);
CREATE INDEX idx_support_tickets_priority ON support_tickets (priority);
CREATE INDEX idx_support_tickets_assigned_to ON support_tickets (assigned_to);

CREATE INDEX idx_integrations_organization_id ON integrations (organization_id);
CREATE INDEX idx_integrations_provider ON integrations (provider);
CREATE INDEX idx_integrations_enabled ON integrations (active);

CREATE INDEX idx_audit_logs_user_id ON audit_logs (user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs (action);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs (entity_type);
CREATE INDEX idx_audit_logs_entity_id ON audit_logs (entity_id);
CREATE INDEX idx_audit_logs_organization_id ON audit_logs (organization_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs (created_at);
