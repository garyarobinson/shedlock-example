-- Initial schema migration
-- Creates core tables for the application including ShedLock table

-- Create ShedLock table for distributed locking
CREATE TABLE shedlock
(
    name       VARCHAR(64)  NOT NULL,
    lock_until TIMESTAMP    NOT NULL,
    locked_at  TIMESTAMP    NOT NULL,
    locked_by  VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);

CREATE INDEX idx_shedlock_lock_until ON shedlock (lock_until);

-- Create audit_logs table for tracking user activities
CREATE TABLE audit_logs
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       VARCHAR(100) NOT NULL,
    action        VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100),
    resource_id   VARCHAR(100),
    details       TEXT,
    ip_address    VARCHAR(100),
    user_agent    TEXT,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    session_id    VARCHAR(100)
);

CREATE INDEX idx_audit_logs_created_at ON audit_logs (created_at);
CREATE INDEX idx_audit_logs_user_id ON audit_logs (user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs (action);

-- Insert seed data into audit_logs table
INSERT INTO audit_logs (user_id, action, resource_type, resource_id, details, ip_address, user_agent, created_at,
                        session_id)
VALUES ('user1', 'LOGIN', 'USER', '1', 'User logged in', '192.168.1.1', 'Mozilla/5.0',
        CURRENT_TIMESTAMP - INTERVAL '1 day', 'session1'),
       ('user2', 'LOGOUT', 'USER', '2', 'User logged out', '192.168.1.2', 'Mozilla/5.0',
        CURRENT_TIMESTAMP - INTERVAL '2 days', 'session2'),
       ('user3', 'CREATE', 'DOCUMENT', '3', 'Document created', '192.168.1.3', 'Mozilla/5.0',
        CURRENT_TIMESTAMP - INTERVAL '3 days', 'session3'),
       ('user4', 'DELETE', 'DOCUMENT', '4', 'Document deleted', '192.168.1.4', 'Mozilla/5.0',
        CURRENT_TIMESTAMP - INTERVAL '4 days', 'session4'),
       ('user5', 'UPDATE', 'PROFILE', '5', 'Profile updated', '192.168.1.5', 'Mozilla/5.0',
        CURRENT_TIMESTAMP - INTERVAL '5 days', 'session5'),
       ('user6', 'LOGIN', 'USER', '6', 'User logged in', '192.168.1.6', 'Mozilla/5.0',
        CURRENT_TIMESTAMP - INTERVAL '91 days', 'session6'),
       ('user7', 'LOGOUT', 'USER', '7', 'User logged out', '192.168.1.7', 'Mozilla/5.0',
        CURRENT_TIMESTAMP - INTERVAL '92 days', 'session7'),
       ('user8', 'CREATE', 'DOCUMENT', '8', 'Document created', '192.168.1.8', 'Mozilla/5.0',
        CURRENT_TIMESTAMP - INTERVAL '93 days', 'session8'),
       ('user9', 'DELETE', 'DOCUMENT', '9', 'Document deleted', '192.168.1.9', 'Mozilla/5.0',
        CURRENT_TIMESTAMP - INTERVAL '94 days', 'session9');