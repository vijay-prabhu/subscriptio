package com.subscriptio.domain.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "webhook_events")
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stripe_event_id", nullable = false, unique = true)
    private String stripeEventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "JSONB")
    private String payload;

    @Column(nullable = false)
    private String status = "pending";

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected WebhookEvent() {
    }

    public WebhookEvent(String stripeEventId, String eventType, String payload) {
        this.stripeEventId = stripeEventId;
        this.eventType = eventType;
        this.payload = payload;
    }

    public void markProcessed() {
        this.status = "processed";
        this.processedAt = Instant.now();
    }

    public void markFailed(String error) {
        this.status = "failed";
        this.errorMessage = error;
    }

    public boolean isProcessed() {
        return "processed".equals(status);
    }

    public Long getId() { return id; }
    public String getStripeEventId() { return stripeEventId; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public String getStatus() { return status; }
    public Instant getProcessedAt() { return processedAt; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getCreatedAt() { return createdAt; }
}
