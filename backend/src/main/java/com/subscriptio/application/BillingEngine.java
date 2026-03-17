package com.subscriptio.application;

import com.subscriptio.domain.model.Invoice;
import com.subscriptio.domain.model.Subscription;
import com.subscriptio.domain.model.SubscriptionStatus;
import com.subscriptio.domain.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BillingEngine {

    private static final Logger log = LoggerFactory.getLogger(BillingEngine.class);

    private final SubscriptionRepository subscriptionRepo;
    private final InvoiceService invoiceService;

    public BillingEngine(SubscriptionRepository subscriptionRepo, InvoiceService invoiceService) {
        this.subscriptionRepo = subscriptionRepo;
        this.invoiceService = invoiceService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void processBillingCycle() {
        log.info("Starting billing cycle");
        List<Subscription> dueSubscriptions = subscriptionRepo.findDueForBilling(Instant.now());
        log.info("Found {} subscriptions due for billing", dueSubscriptions.size());

        int invoicesGenerated = 0;
        int errors = 0;

        for (Subscription subscription : dueSubscriptions) {
            try {
                processSubscription(subscription);
                invoicesGenerated++;
            } catch (Exception e) {
                errors++;
                log.error("Failed to process subscription {}: {}", subscription.getExternalId(), e.getMessage());
            }
        }

        log.info("Billing cycle complete. Invoices generated: {}, Errors: {}", invoicesGenerated, errors);
    }

    @Transactional
    public void processSubscription(Subscription subscription) {
        if (subscription.getStatus() == SubscriptionStatus.TRIALING) {
            handleTrialEnd(subscription);
            return;
        }

        if (subscription.isCancelAtPeriodEnd()) {
            subscription.cancel();
            subscriptionRepo.save(subscription);
            log.info("Subscription {} cancelled at period end", subscription.getExternalId());
            return;
        }

        Invoice invoice = invoiceService.generateInvoice(subscription);
        log.info("Generated invoice {} for subscription {}",
                invoice.getInvoiceNumber(), subscription.getExternalId());

        advanceBillingPeriod(subscription);
    }

    private void handleTrialEnd(Subscription subscription) {
        subscription.activate();

        if (subscription.isCancelAtPeriodEnd()) {
            subscription.cancel();
            subscriptionRepo.save(subscription);
            log.info("Trial subscription {} cancelled", subscription.getExternalId());
            return;
        }

        Invoice invoice = invoiceService.generateInvoice(subscription);
        log.info("Trial ended for subscription {}. Generated invoice {}",
                subscription.getExternalId(), invoice.getInvoiceNumber());

        advanceBillingPeriod(subscription);
    }

    private void advanceBillingPeriod(Subscription subscription) {
        Instant newStart = subscription.getCurrentPeriodEnd();
        Instant newEnd = "yearly".equals(subscription.getPlan().getBillingInterval())
                ? newStart.plus(365, ChronoUnit.DAYS)
                : newStart.plus(30, ChronoUnit.DAYS);

        subscription.advanceBillingPeriod(newStart, newEnd);
        subscriptionRepo.save(subscription);
    }
}
