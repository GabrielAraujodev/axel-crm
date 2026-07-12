-- V25__create_analytics_views.sql
-- CQRS views for analytics — remove dependency on domain repositories.
-- These views are OWNED by analytics; domain tables can change independently.

CREATE VIEW vw_entity_counts AS
SELECT
    o.id AS organization_id,
    (SELECT COUNT(*) FROM leads l WHERE l.organization_id = o.id AND l.deleted_at IS NULL)                   AS total_leads,
    (SELECT COUNT(*) FROM clients c WHERE c.organization_id = o.id AND c.deleted_at IS NULL)                 AS total_clients,
    (SELECT COUNT(*) FROM deals d WHERE d.organization_id = o.id AND d.deleted_at IS NULL)                   AS total_deals,
    (SELECT COUNT(*) FROM deals d WHERE d.organization_id = o.id AND d.deleted_at IS NULL AND d.won IS NULL) AS open_deals,
    (SELECT COUNT(*) FROM deals d WHERE d.organization_id = o.id AND d.deleted_at IS NULL AND d.won = TRUE)  AS won_deals,
    (SELECT COALESCE(SUM(d.value), 0) FROM deals d WHERE d.organization_id = o.id AND d.deleted_at IS NULL AND d.won IS NULL) AS pipeline_value,
    (SELECT COUNT(*) FROM tasks t WHERE t.organization_id = o.id AND t.status = 'PENDING')                   AS tasks_pending,
    (SELECT COUNT(*) FROM tasks t WHERE t.organization_id = o.id AND t.status = 'COMPLETED')                 AS tasks_completed,
    (SELECT COUNT(*) FROM projects p WHERE p.organization_id = o.id AND p.deleted_at IS NULL)                AS total_projects,
    (SELECT COUNT(*) FROM proposals p WHERE p.organization_id = o.id AND p.deleted_at IS NULL)               AS total_proposals,
    (SELECT COUNT(*) FROM campaigns c WHERE c.organization_id = o.id AND c.deleted_at IS NULL)               AS total_campaigns,
    (SELECT COUNT(*) FROM support_tickets s WHERE s.organization_id = o.id)                                 AS total_support_tickets
FROM organizations o;

CREATE VIEW vw_deal_pipeline AS
SELECT
    d.id,
    d.organization_id,
    d.value,
    d.won,
    ps.name     AS stage_name,
    ps.win_probability
FROM deals d
LEFT JOIN pipeline_stages ps ON ps.id = d.stage_id
WHERE d.deleted_at IS NULL;

CREATE VIEW vw_lead_analytics AS
SELECT
    l.id,
    l.organization_id,
    l.stage,
    l.source,
    CASE WHEN l.stage = 'CONVERTED' THEN 1 ELSE 0 END AS is_converted
FROM leads l
WHERE l.deleted_at IS NULL;

CREATE VIEW vw_monthly_financial AS
SELECT
    ft.organization_id,
    EXTRACT(YEAR FROM ft.transaction_date)  AS year,
    EXTRACT(MONTH FROM ft.transaction_date) AS month,
    ft.type,
    ft.amount,
    ft.paid,
    ft.transaction_date
FROM financial_transactions ft
WHERE ft.deleted_at IS NULL;

CREATE VIEW vw_project_profitability AS
SELECT
    p.id,
    p.organization_id,
    p.name,
    p.status,
    p.budget,
    p.cost,
    c.name AS client_name
FROM projects p
LEFT JOIN clients c ON c.id = p.client_id AND c.deleted_at IS NULL
WHERE p.deleted_at IS NULL;

CREATE VIEW vw_proposal_analytics AS
SELECT
    p.id,
    p.organization_id,
    p.status,
    p.total_amount
FROM proposals p
WHERE p.deleted_at IS NULL;
