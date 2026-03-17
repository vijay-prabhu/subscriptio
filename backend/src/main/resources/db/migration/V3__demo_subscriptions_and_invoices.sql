-- Demo subscriptions, invoices, and payments for UI testing
-- Creates a realistic dataset across the 3 seeded customers and 5 plans

-- Subscriptions: Alice on Pro (active), Bob on Starter (trialing), Carol on Enterprise (active)
INSERT INTO subscriptions (tenant_id, customer_id, plan_id, status, current_period_start, current_period_end, trial_end, cancel_at_period_end)
VALUES
    (1, 1, 2, 'ACTIVE',   now() - interval '60 days', now() + interval '30 days', now() - interval '46 days', false),
    (1, 2, 1, 'TRIALING', now() - interval '3 days',  now() + interval '11 days', now() + interval '11 days', false),
    (1, 3, 3, 'ACTIVE',   now() - interval '30 days', now() + interval '30 days', null, false);

-- Past invoices for Alice (Pro $49.99 x 2 months)
INSERT INTO invoices (tenant_id, customer_id, subscription_id, invoice_number, status, subtotal, tax, total, currency, due_date, paid_at, period_start, period_end)
VALUES
    (1, 1, 1, 'INV-2026-0001', 'PAID', 49.99, 0, 49.99, 'USD',
     now() - interval '46 days', now() - interval '45 days',
     now() - interval '60 days', now() - interval '30 days'),
    (1, 1, 1, 'INV-2026-0002', 'PAID', 49.99, 0, 49.99, 'USD',
     now() - interval '16 days', now() - interval '15 days',
     now() - interval '30 days', now());

-- Invoices for Carol (Enterprise $99.99 x 1 month)
INSERT INTO invoices (tenant_id, customer_id, subscription_id, invoice_number, status, subtotal, tax, total, currency, due_date, paid_at, period_start, period_end)
VALUES
    (1, 3, 3, 'INV-2026-0003', 'PAID', 99.99, 0, 99.99, 'USD',
     now() - interval '16 days', now() - interval '14 days',
     now() - interval '30 days', now());

-- Open invoice for next cycle (Alice)
INSERT INTO invoices (tenant_id, customer_id, subscription_id, invoice_number, status, subtotal, tax, total, currency, due_date, period_start, period_end)
VALUES
    (1, 1, 1, 'INV-2026-0004', 'OPEN', 49.99, 0, 49.99, 'USD',
     now() + interval '14 days',
     now(), now() + interval '30 days');

-- Line items for all invoices
INSERT INTO invoice_line_items (invoice_id, description, quantity, unit_price, amount)
VALUES
    (1, 'Pro Plan - Jan 2026', 1, 49.99, 49.99),
    (2, 'Pro Plan - Feb 2026', 1, 49.99, 49.99),
    (3, 'Enterprise Plan - Feb 2026', 1, 99.99, 99.99),
    (4, 'Pro Plan - Mar 2026', 1, 49.99, 49.99);

-- Payments for paid invoices
INSERT INTO payments (tenant_id, invoice_id, amount, currency, status)
VALUES
    (1, 1, 49.99, 'USD', 'SUCCEEDED'),
    (1, 2, 49.99, 'USD', 'SUCCEEDED'),
    (1, 3, 99.99, 'USD', 'SUCCEEDED');
