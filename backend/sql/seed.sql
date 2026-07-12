-- Seed data for Axel CRM
-- Run after the application has started and Flyway migrations have been applied.

-- Default organization
INSERT INTO organizations (id, name, domain, active, created_at, updated_at)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Axel Empreendimentos', 'axelcrm.com', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Default admin user (password: admin123, bcrypt encoded)
INSERT INTO users (id, organization_id, email, password, name, role, active, created_at, updated_at)
VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'admin@axelcrm.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrttPmYylG5RB/KVJhT8N3Kj3cXuQG',
    'Administrador',
    'SUPER_ADMIN',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (email) DO NOTHING;

-- Default pipeline for sales
INSERT INTO pipelines (id, organization_id, name, created_at, updated_at)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Vendas Padrão', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO pipeline_stages (id, organization_id, pipeline_id, name, position, created_at, updated_at)
VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Novo', 1, NOW(), NOW()),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380b02', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Contatado', 2, NOW(), NOW()),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380b03', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Qualificado', 3, NOW(), NOW()),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380b04', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Proposta', 4, NOW(), NOW()),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380b05', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Fechado', 5, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
