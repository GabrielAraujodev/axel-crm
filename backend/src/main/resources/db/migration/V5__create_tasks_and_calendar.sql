-- V5__create_tasks_and_calendar.sql
-- Tasks and calendar events.

CREATE TABLE tasks
(
    id               UUID PRIMARY KEY,
    organization_id  UUID NOT NULL,
    project_id       UUID,
    title            VARCHAR(200) NOT NULL,
    description      TEXT,
    status           VARCHAR(50)  DEFAULT 'PENDING',
    priority         VARCHAR(20)  DEFAULT 'MEDIUM',
    assigned_to      UUID,
    due_date         DATE,
    estimated_hours  DECIMAL(8, 2),
    created_by       UUID,
    created_at       TIMESTAMP    DEFAULT NOW(),
    updated_at       TIMESTAMP,
    deleted_at       TIMESTAMP,
    CONSTRAINT fk_tasks_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects (id)
);

CREATE TABLE calendar_events
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    user_id         UUID NOT NULL,
    lead_id         UUID,
    client_id       UUID,
    deal_id         UUID,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    start_time      TIMESTAMP NOT NULL,
    end_time        TIMESTAMP NOT NULL,
    all_day         BOOLEAN        DEFAULT false,
    location        VARCHAR(255),
    created_at      TIMESTAMP      DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_calendar_events_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_calendar_events_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_calendar_events_lead FOREIGN KEY (lead_id) REFERENCES leads (id),
    CONSTRAINT fk_calendar_events_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_calendar_events_deal FOREIGN KEY (deal_id) REFERENCES deals (id)
);

CREATE INDEX idx_tasks_organization_id ON tasks (organization_id);
CREATE INDEX idx_tasks_project_id ON tasks (project_id);
CREATE INDEX idx_tasks_assigned_to ON tasks (assigned_to);
CREATE INDEX idx_tasks_status ON tasks (status);
CREATE INDEX idx_tasks_priority ON tasks (priority);
CREATE INDEX idx_tasks_due_date ON tasks (due_date);

CREATE INDEX idx_calendar_events_organization_id ON calendar_events (organization_id);
CREATE INDEX idx_calendar_events_user_id ON calendar_events (user_id);
CREATE INDEX idx_calendar_events_lead_id ON calendar_events (lead_id);
CREATE INDEX idx_calendar_events_start_time ON calendar_events (start_time);
CREATE INDEX idx_calendar_events_end_time ON calendar_events (end_time);
