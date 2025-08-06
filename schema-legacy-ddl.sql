-- Create table resource a
CREATE TABLE resource_a (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255)
);

-- Create table outbox table
CREATE TABLE outbox (
    id VARCHAR(255) PRIMARY KEY,
    event_body VARCHAR,
    created_at TIMESTAMP
);