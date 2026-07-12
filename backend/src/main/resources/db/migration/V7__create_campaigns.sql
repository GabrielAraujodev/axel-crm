-- V7__create_campaigns.sql
-- Marketing campaigns.

CREATE TABLE campaigns
(
    id               UUID PRIMARY KEY,
    organization_id  UUID NOT NULL,
    name             VARCHAR(200) NOT NULL,
    type             VARCHAR(50) NOT NULL,
    content          TEXT,
    scheduled_at     TIMESTAMP,
    sent_at          TIMESTAMP,
    recipients_count INT DEFAULT 0,
    sent_count       INT DEFAULT 0,
    open_count       INT DEFAULT 0,
    click_count      INT DEFAULT 0,
    status           VARCHAR(50) DEFAULT 'RASCUNHO',
    created_by       UUID,
    created_at       TIMESTAMP   DEFAULT NOW(),
    updated_at       TIMESTAMP,
    deleted_at       TIMESTAMP,
    CONSTRAINT fk_campaigns_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_campaigns_user FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE INDEX idx_campaigns_organization_id ON campaigns (organization_id);
CREATE INDEX idx_campaigns_type ON campaigns (type);
CREATE INDEX idx_campaigns_status ON campaigns (status);
CREATE INDEX idx_campaigns_scheduled_at ON campaigns (scheduled_at);
CREATE INDEX idx_campaigns_sent_at ON campaigns (sent_at);
