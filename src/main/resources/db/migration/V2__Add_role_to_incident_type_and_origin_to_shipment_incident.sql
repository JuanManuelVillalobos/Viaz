-- V2__Add_role_to_incident_type_and_origin_to_shipment_incident.sql

-- Add 'role' column to the incident_types table to distinguish 'carrier' and 'client'
ALTER TABLE viaz.public.incidents
ADD COLUMN entityTied VARCHAR(255) NOT NULL;

-- Add 'origin' column to the shipment_incidents table
ALTER TABLE shipment_incidents
ADD COLUMN origin VARCHAR(255) NOT NULL;

-- You can add initial data or sample rows for 'carrier' and 'client' roles
INSERT INTO incident_types (name, penalty, role) VALUES ('Carrier Delay', 10.0, 'carrier');
INSERT INTO incident_types (name, penalty, role) VALUES ('Client Complaint', 0.0, 'client');
