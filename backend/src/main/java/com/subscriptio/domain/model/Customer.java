package com.subscriptio.domain.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "email"})
})
public class Customer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Subscription> subscriptions = new ArrayList<>();

    protected Customer() {
    }

    public Customer(Tenant tenant, String email, String name) {
        this.tenant = tenant;
        this.email = email;
        this.name = name;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }
}
