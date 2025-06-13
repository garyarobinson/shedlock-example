-- Insert 10 example statements into app_version table

INSERT INTO app_version (organization_id, session_metadata, environment, session_id, agent_reporting_instance_id, application_name, application_id, last_read)
VALUES
('org-alpha', '{"user_id": "user1", "feature_flags": ["new_ui", "beta_feature"]}', 'production', 'session-abc-001', 'agent-prod-srv1-001', 'E-Commerce Platform', 'app-ec-001', NOW() - INTERVAL '1 hour'),
('org-beta', '{"user_id": "user2", "ip_address": "192.168.0.101"}', 'staging', 'session-def-002', 'agent-stag-srv2-002', 'Inventory Management', 'app-im-002', NOW() - INTERVAL '2 days'),
('org-gamma', '{"theme": "dark", "language": "en"}', 'development', 'session-ghi-003', 'agent-dev-lap1-003', 'Reporting Dashboard', 'app-rd-003', NOW() - INTERVAL '5 minutes'),
('org-delta', '{"customer_tier": "premium"}', 'production', 'session-jkl-004', 'agent-prod-srv1-004', 'Mobile Banking App', 'app-mb-004', NOW() - INTERVAL '30 minutes'),
('org-epsilon', '{"ab_test_group": "A"}', 'production', 'session-mno-005', 'agent-prod-srv2-005', 'Social Media Connector', 'app-sm-005', NOW() - INTERVAL '3 hours'),
('org-zeta', '{"device_type": "tablet"}', 'staging', 'session-pqr-006', 'agent-stag-srv1-006', 'Logistics Optimizer', 'app-lo-006', NOW() - INTERVAL '1 day'),
('org-eta', '{"session_start_time": "2025-06-13T10:00:00Z"}', 'development', 'session-stu-007', 'agent-dev-lap2-007', 'Customer Portal', 'app-cp-007', NOW() - INTERVAL '10 seconds'),
('org-theta', '{"internal_user": "true"}', 'production', 'session-vwx-008', 'agent-prod-srv3-008', 'API Gateway', 'app-ag-008', NOW() - INTERVAL '45 minutes'),
('org-iota', '{"campaign_id": "summer_sale_2025"}', 'production', 'session-yz0-009', 'agent-prod-srv1-009', 'Marketing Analytics', 'app-ma-009', NOW() - INTERVAL '6 hours'),
('org-kappa', '{"user_role": "admin"}', 'staging', 'session-123-010', 'agent-stag-srv2-010', 'Admin Console', 'app-ac-010', NOW() - INTERVAL '12 hours');

