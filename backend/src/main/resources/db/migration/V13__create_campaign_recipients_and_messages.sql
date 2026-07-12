-- V13__create_campaign_recipients_and_messages.sql
-- Create tables for campaign recipients and multi-channel message history

CREATE TABLE campaign_recipients
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID NOT NULL,
    campaign_id     UUID NOT NULL,
    lead_id         UUID,
    client_id       UUID,
    contact_id      UUID,
    email           VARCHAR(255),
    phone           VARCHAR(50),
    status          VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    sent_at         TIMESTAMP,
    opened_at       TIMESTAMP,
    clicked_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_campaign_recipients_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_campaign_recipients_campaign FOREIGN KEY (campaign_id) REFERENCES campaigns (id),
    CONSTRAINT fk_campaign_recipients_lead FOREIGN KEY (lead_id) REFERENCES leads (id),
    CONSTRAINT fk_campaign_recipients_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_campaign_recipients_contact FOREIGN KEY (contact_id) REFERENCES contacts (id)
);

CREATE INDEX idx_campaign_recipients_org ON campaign_recipients (organization_id);
CREATE INDEX idx_campaign_recipients_campaign ON campaign_recipients (campaign_id);

CREATE TABLE messages
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID NOT NULL,
    lead_id         UUID,
    client_id       UUID,
    user_id         UUID,
    channel         VARCHAR(50) NOT NULL,
    direction       VARCHAR(50) NOT NULL,
    sender          VARCHAR(255) NOT NULL,
    recipient       VARCHAR(255) NOT NULL,
    subject         VARCHAR(255),
    body            TEXT NOT NULL,
    status          VARCHAR(50) NOT NULL DEFAULT 'SENT',
    sent_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_messages_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_messages_lead FOREIGN KEY (lead_id) REFERENCES leads (id),
    CONSTRAINT fk_messages_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_messages_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_messages_org ON messages (organization_id);
CREATE INDEX idx_messages_lead ON messages (lead_id);
CREATE INDEX idx_messages_client ON messages (client_id);
CREATE INDEX idx_messages_channel ON messages (channel);
