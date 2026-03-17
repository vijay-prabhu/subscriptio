package com.subscriptio.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "invoice_number"})
})
public class Invoice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private String currency = "USD";

    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "period_start", nullable = false)
    private Instant periodStart;

    @Column(name = "period_end", nullable = false)
    private Instant periodEnd;

    @Column(name = "stripe_invoice_id")
    private String stripeInvoiceId;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<InvoiceLineItem> lineItems = new ArrayList<>();

    protected Invoice() {
    }

    public Invoice(Tenant tenant, Customer customer, Subscription subscription,
                   String invoiceNumber, BigDecimal subtotal, BigDecimal total,
                   Instant dueDate, Instant periodStart, Instant periodEnd) {
        this.tenant = tenant;
        this.customer = customer;
        this.subscription = subscription;
        this.invoiceNumber = invoiceNumber;
        this.subtotal = subtotal;
        this.total = total;
        this.dueDate = dueDate;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    public void addLineItem(InvoiceLineItem lineItem) {
        lineItems.add(lineItem);
        lineItem.setInvoice(this);
    }

    public void markOpen() {
        this.status = InvoiceStatus.OPEN;
    }

    public void markPaid() {
        this.status = InvoiceStatus.PAID;
        this.paidAt = Instant.now();
    }

    public void markVoid() {
        this.status = InvoiceStatus.VOID;
    }

    public void markUncollectible() {
        this.status = InvoiceStatus.UNCOLLECTIBLE;
    }

    // Getters

    public Tenant getTenant() { return tenant; }
    public Customer getCustomer() { return customer; }
    public Subscription getSubscription() { return subscription; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public InvoiceStatus getStatus() { return status; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTax() { return tax; }
    public BigDecimal getTotal() { return total; }
    public String getCurrency() { return currency; }
    public Instant getDueDate() { return dueDate; }
    public Instant getPaidAt() { return paidAt; }
    public Instant getPeriodStart() { return periodStart; }
    public Instant getPeriodEnd() { return periodEnd; }
    public String getStripeInvoiceId() { return stripeInvoiceId; }
    public void setStripeInvoiceId(String stripeInvoiceId) { this.stripeInvoiceId = stripeInvoiceId; }
    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
    public List<InvoiceLineItem> getLineItems() { return lineItems; }
}
