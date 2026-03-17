package com.subscriptio.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "plans")
public class Plan extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String currency = "USD";

    @Column(name = "billing_interval", nullable = false)
    private String billingInterval;

    @Column(name = "trial_days", nullable = false)
    private int trialDays = 0;

    @Column(name = "features", columnDefinition = "JSONB")
    private String features = "[]";

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "stripe_price_id")
    private String stripePriceId;

    protected Plan() {
    }

    public Plan(Tenant tenant, String name, BigDecimal price, String billingInterval) {
        this.tenant = tenant;
        this.name = name;
        this.price = price;
        this.billingInterval = billingInterval;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBillingInterval() {
        return billingInterval;
    }

    public int getTrialDays() {
        return trialDays;
    }

    public void setTrialDays(int trialDays) {
        this.trialDays = trialDays;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getStripePriceId() {
        return stripePriceId;
    }

    public void setStripePriceId(String stripePriceId) {
        this.stripePriceId = stripePriceId;
    }

    public BigDecimal getMonthlyPrice() {
        if ("yearly".equals(billingInterval)) {
            return price.divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP);
        }
        return price;
    }
}
