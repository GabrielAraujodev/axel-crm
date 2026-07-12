-- Migration to add contact type to contacts and service type to clients

ALTER TABLE contacts ADD COLUMN contact_type VARCHAR(50) DEFAULT 'OTHER';
ALTER TABLE clients ADD COLUMN service_type VARCHAR(50);
