package com.subscriptio.application;

import com.subscriptio.domain.model.WebhookEvent;
import com.subscriptio.infrastructure.persistence.SpringDataWebhookEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final SpringDataWebhookEventRepository webhookRepo;

    public WebhookService(SpringDataWebhookEventRepository webhookRepo) {
        this.webhookRepo = webhookRepo;
    }

    @Transactional
    public boolean processEvent(String stripeEventId, String eventType, String payload) {
        // Idempotency check
        var existing = webhookRepo.findByStripeEventId(stripeEventId);
        if (existing.isPresent() && existing.get().isProcessed()) {
            log.info("Event {} already processed, skipping", stripeEventId);
            return true;
        }

        WebhookEvent event = existing.orElseGet(() ->
                webhookRepo.save(new WebhookEvent(stripeEventId, eventType, payload)));

        try {
            handleEvent(eventType, payload);
            event.markProcessed();
            webhookRepo.save(event);
            log.info("Successfully processed webhook event: {} ({})", stripeEventId, eventType);
            return true;
        } catch (Exception e) {
            event.markFailed(e.getMessage());
            webhookRepo.save(event);
            log.error("Failed to process webhook event: {} ({}): {}", stripeEventId, eventType, e.getMessage());
            return false;
        }
    }

    private void handleEvent(String eventType, String payload) {
        switch (eventType) {
            case "invoice.paid" -> handleInvoicePaid(payload);
            case "invoice.payment_failed" -> handleInvoicePaymentFailed(payload);
            case "customer.subscription.updated" -> handleSubscriptionUpdated(payload);
            case "customer.subscription.deleted" -> handleSubscriptionDeleted(payload);
            default -> log.info("Unhandled event type: {}", eventType);
        }
    }

    private void handleInvoicePaid(String payload) {
        log.info("Processing invoice.paid event");
        // In a full implementation: find invoice by stripe_invoice_id, mark as paid,
        // update subscription status if needed
    }

    private void handleInvoicePaymentFailed(String payload) {
        log.info("Processing invoice.payment_failed event");
        // In a full implementation: find invoice, mark subscription as past_due
    }

    private void handleSubscriptionUpdated(String payload) {
        log.info("Processing customer.subscription.updated event");
        // In a full implementation: sync subscription status with Stripe
    }

    private void handleSubscriptionDeleted(String payload) {
        log.info("Processing customer.subscription.deleted event");
        // In a full implementation: cancel subscription
    }
}
