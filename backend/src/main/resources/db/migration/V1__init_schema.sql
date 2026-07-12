-- V1__init_schema.sql
-- Initial schema: organizations and users with multi-tenant support.

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE organizations
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name       VARCHAR(200) NOT NULL,
    domain     VARCHAR(255) UNIQUE,
    document   VARCHAR(20),
    phone      VARCHAR(20),
    email      VARCHAR(255),
    address    TEXT,
    active     BOOLEAN          DEFAULT true,
    created_at TIMESTAMP        DEFAULT NOW(),
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE users
(
    id              UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    name            VARCHAR(200) NOT NULL,
    phone           VARCHAR(20),
    role            VARCHAR(50)  NOT NULL DEFAULT 'USER',
    avatar_url      TEXT,
    active          BOOLEAN               DEFAULT true,
    created_at      TIMESTAMP             DEFAULT NOW(),
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    CONSTRAINT fk_users_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE INDEX idx_users_organization_id ON users (organization_id);
CREATE INDEX idx_users_email ON users (email);
