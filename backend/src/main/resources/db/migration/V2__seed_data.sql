-- Demo tenant and plans for development/testing

INSERT INTO tenants (name, slug, status) VALUES ('Demo SaaS Corp', 'demo-saas', 'active');

INSERT INTO plans (tenant_id, name, description, price, currency, billing_interval, trial_days, features)
VALUES
    (1, 'Starter', 'For small teams getting started', 29.99, 'USD', 'monthly', 14, '["5_users", "basic_support", "1gb_storage"]'),
    (1, 'Pro', 'For growing businesses', 49.99, 'USD', 'monthly', 14, '["unlimited_users", "priority_support", "10gb_storage", "api_access"]'),
    (1, 'Enterprise', 'For large organizations', 99.99, 'USD', 'monthly', 0, '["unlimited_users", "dedicated_support", "unlimited_storage", "api_access", "sso", "custom_branding"]'),
    (1, 'Starter Annual', 'Starter plan billed yearly', 299.99, 'USD', 'yearly', 14, '["5_users", "basic_support", "1gb_storage"]'),
    (1, 'Pro Annual', 'Pro plan billed yearly', 499.99, 'USD', 'yearly', 14, '["unlimited_users", "priority_support", "10gb_storage", "api_access"]');

INSERT INTO customers (tenant_id, email, name)
VALUES
    (1, 'alice@example.com', 'Alice Johnson'),
    (1, 'bob@example.com', 'Bob Smith'),
    (1, 'carol@example.com', 'Carol Williams');
