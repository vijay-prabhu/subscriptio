package com.subscriptio.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_methods")
public class PaymentMethod extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "stripe_payment_method_id", nullable = false)
    private String stripePaymentMethodId;

    @Column(nullable = false)
    private String type;

    @Column(name = "last_four")
    private String lastFour;

    private String brand;

    @Column(name = "exp_month")
    private Integer expMonth;

    @Column(name = "exp_year")
    private Integer expYear;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    protected PaymentMethod() {
    }

    public PaymentMethod(Tenant tenant, Customer customer, String stripePaymentMethodId, String type) {
        this.tenant = tenant;
        this.customer = customer;
        this.stripePaymentMethodId = stripePaymentMethodId;
        this.type = type;
    }

    public Tenant getTenant() { return tenant; }
    public Customer getCustomer() { return customer; }
    public String getStripePaymentMethodId() { return stripePaymentMethodId; }
    public String getType() { return type; }
    public String getLastFour() { return lastFour; }
    public void setLastFour(String lastFour) { this.lastFour = lastFour; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public Integer getExpMonth() { return expMonth; }
    public void setExpMonth(Integer expMonth) { this.expMonth = expMonth; }
    public Integer getExpYear() { return expYear; }
    public void setExpYear(Integer expYear) { this.expYear = expYear; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
}
