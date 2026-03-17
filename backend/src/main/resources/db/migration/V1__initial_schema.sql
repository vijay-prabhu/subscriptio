-- SubscriptIO Initial Schema
-- Follows PostgreSQL best practices: BIGINT identity PKs, TIMESTAMPTZ, NUMERIC for money, TEXT for strings

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- TENANTS
-- ============================================================
CREATE TABLE tenants (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    external_id UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    name        TEXT NOT NULL,
    slug        TEXT NOT NULL UNIQUE,
    stripe_account_id TEXT,
    status      TEXT NOT NULL DEFAULT 'active'
        CHECK (status IN ('active', 'suspended', 'churned')),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    version     BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_tenants_status ON tenants (status) WHERE status = 'active';

-- ============================================================
-- PLANS
-- ============================================================
CREATE TABLE plans (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    external_id      UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    tenant_id        BIGINT NOT NULL REFERENCES tenants(id),
    name             TEXT NOT NULL,
    description      TEXT,
    price            NUMERIC(12,2) NOT NULL,
    currency         TEXT NOT NULL DEFAULT 'USD',
    billing_interval TEXT NOT NULL CHECK (billing_interval IN ('monthly', 'yearly')),
    trial_days       INT NOT NULL DEFAULT 0,
    features         JSONB NOT NULL DEFAULT '[]',
    is_active        BOOLEAN NOT NULL DEFAULT true,
    stripe_price_id  TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    version          BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_plans_tenant_id ON plans (tenant_id);
CREATE INDEX idx_plans_active ON plans (tenant_id, is_active) WHERE is_active = true;

-- ============================================================
-- CUSTOMERS
-- ============================================================
CREATE TABLE customers (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    external_id       UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    tenant_id         BIGINT NOT NULL REFERENCES tenants(id),
    email             TEXT NOT NULL,
    name              TEXT NOT NULL,
    stripe_customer_id TEXT,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    version           BIGINT NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, email)
);

CREATE INDEX idx_customers_tenant_id ON customers (tenant_id);
CREATE INDEX idx_customers_stripe ON customers (stripe_customer_id) WHERE stripe_customer_id IS NOT NULL;

-- ============================================================
-- SUBSCRIPTIONS
-- ============================================================
CREATE TABLE subscriptions (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    external_id          UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    tenant_id            BIGINT NOT NULL REFERENCES tenants(id),
    customer_id          BIGINT NOT NULL REFERENCES customers(id),
    plan_id              BIGINT NOT NULL REFERENCES plans(id),
    status               TEXT NOT NULL DEFAULT 'TRIALING'
        CHECK (status IN ('TRIALING', 'ACTIVE', 'PAST_DUE', 'CANCELLED', 'EXPIRED')),
    current_period_start TIMESTAMPTZ NOT NULL,
    current_period_end   TIMESTAMPTZ NOT NULL,
    trial_end            TIMESTAMPTZ,
    cancelled_at         TIMESTAMPTZ,
    cancel_at_period_end BOOLEAN NOT NULL DEFAULT false,
    stripe_subscription_id TEXT,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    version              BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_subscriptions_tenant_id ON subscriptions (tenant_id);
CREATE INDEX idx_subscriptions_customer ON subscriptions (customer_id);
CREATE INDEX idx_subscriptions_status ON subscriptions (tenant_id, status);
CREATE INDEX idx_subscriptions_billing ON subscriptions (current_period_end)
    WHERE status IN ('ACTIVE', 'TRIALING');

-- ============================================================
-- INVOICES
-- ============================================================
CREATE TABLE invoices (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    external_id     UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    tenant_id       BIGINT NOT NULL REFERENCES tenants(id),
    customer_id     BIGINT NOT NULL REFERENCES customers(id),
    subscription_id BIGINT NOT NULL REFERENCES subscriptions(id),
    invoice_number  TEXT NOT NULL,
    status          TEXT NOT NULL DEFAULT 'DRAFT'
        CHECK (status IN ('DRAFT', 'OPEN', 'PAID', 'VOID', 'UNCOLLECTIBLE')),
    subtotal        NUMERIC(12,2) NOT NULL,
    tax             NUMERIC(12,2) NOT NULL DEFAULT 0,
    total           NUMERIC(12,2) NOT NULL,
    currency        TEXT NOT NULL DEFAULT 'USD',
    due_date        TIMESTAMPTZ NOT NULL,
    paid_at         TIMESTAMPTZ,
    period_start    TIMESTAMPTZ NOT NULL,
    period_end      TIMESTAMPTZ NOT NULL,
    stripe_invoice_id TEXT,
    pdf_url         TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, invoice_number)
);

CREATE INDEX idx_invoices_tenant_id ON invoices (tenant_id);
CREATE INDEX idx_invoices_customer ON invoices (customer_id);
CREATE INDEX idx_invoices_subscription ON invoices (subscription_id);
CREATE INDEX idx_invoices_status ON invoices (tenant_id, status) WHERE status = 'OPEN';

-- ============================================================
-- INVOICE LINE ITEMS
-- ============================================================
CREATE TABLE invoice_line_items (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    invoice_id  BIGINT NOT NULL REFERENCES invoices(id),
    description TEXT NOT NULL,
    quantity    INT NOT NULL DEFAULT 1,
    unit_price  NUMERIC(12,2) NOT NULL,
    amount      NUMERIC(12,2) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_line_items_invoice ON invoice_line_items (invoice_id);

-- ============================================================
-- PAYMENT METHODS
-- ============================================================
CREATE TABLE payment_methods (
    id                      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    external_id             UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    tenant_id               BIGINT NOT NULL REFERENCES tenants(id),
    customer_id             BIGINT NOT NULL REFERENCES customers(id),
    stripe_payment_method_id TEXT NOT NULL,
    type                    TEXT NOT NULL,
    last_four               TEXT,
    brand                   TEXT,
    exp_month               INT,
    exp_year                INT,
    is_default              BOOLEAN NOT NULL DEFAULT false,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    version                 BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_payment_methods_customer ON payment_methods (customer_id);

-- ============================================================
-- PAYMENTS
-- ============================================================
CREATE TABLE payments (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    external_id         UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    tenant_id           BIGINT NOT NULL REFERENCES tenants(id),
    invoice_id          BIGINT NOT NULL REFERENCES invoices(id),
    payment_method_id   BIGINT REFERENCES payment_methods(id),
    amount              NUMERIC(12,2) NOT NULL,
    currency            TEXT NOT NULL DEFAULT 'USD',
    status              TEXT NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'SUCCEEDED', 'FAILED')),
    stripe_payment_intent_id TEXT,
    failure_reason      TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    version             BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_payments_invoice ON payments (invoice_id);
CREATE INDEX idx_payments_tenant ON payments (tenant_id);

-- ============================================================
-- WEBHOOK EVENTS
-- ============================================================
CREATE TABLE webhook_events (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    stripe_event_id TEXT NOT NULL UNIQUE,
    event_type      TEXT NOT NULL,
    payload         JSONB NOT NULL,
    status          TEXT NOT NULL DEFAULT 'pending'
        CHECK (status IN ('pending', 'processed', 'failed')),
    processed_at    TIMESTAMPTZ,
    error_message   TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_webhook_events_stripe ON webhook_events (stripe_event_id);
CREATE INDEX idx_webhook_events_status ON webhook_events (status) WHERE status = 'pending';

-- ============================================================
-- AUDIT LOG
-- ============================================================
CREATE TABLE audit_logs (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id   BIGINT NOT NULL REFERENCES tenants(id),
    entity_type TEXT NOT NULL,
    entity_id   BIGINT NOT NULL,
    action      TEXT NOT NULL,
    actor       TEXT NOT NULL,
    changes     JSONB,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_tenant_entity ON audit_logs (tenant_id, entity_type, entity_id);
CREATE INDEX idx_audit_created ON audit_logs (created_at);
