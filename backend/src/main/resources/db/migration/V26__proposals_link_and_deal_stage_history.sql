-- V26__proposals_link_and_deal_stage_history.sql
ALTER TABLE proposals ADD COLUMN deal_id UUID CONSTRAINT fk_proposals_deal REFERENCES deals (id);

CREATE TABLE deal_stage_history (
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    deal_id         UUID NOT NULL,
    stage_id        UUID NOT NULL,
    entered_at      TIMESTAMP NOT NULL,
    left_at         TIMESTAMP,
    duration_seconds BIGINT,
    transition_reason TEXT,
    performed_by_id UUID,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_deal_stage_history_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_deal_stage_history_deal FOREIGN KEY (deal_id) REFERENCES deals (id),
    CONSTRAINT fk_deal_stage_history_stage FOREIGN KEY (stage_id) REFERENCES pipeline_stages (id),
    CONSTRAINT fk_deal_stage_history_performed_by FOREIGN KEY (performed_by_id) REFERENCES users (id)
);

CREATE INDEX idx_deal_stage_history_organization_id ON deal_stage_history (organization_id);
CREATE INDEX idx_deal_stage_history_deal_id ON deal_stage_history (deal_id);
