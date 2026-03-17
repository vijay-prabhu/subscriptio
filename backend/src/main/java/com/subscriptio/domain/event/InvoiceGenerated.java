package com.subscriptio.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record InvoiceGenerated(
        UUID invoiceId,
        UUID subscriptionId,
        BigDecimal total,
        Instant occurredAt
) implements DomainEvent {

    public InvoiceGenerated(UUID invoiceId, UUID subscriptionId, BigDecimal total) {
        this(invoiceId, subscriptionId, total, Instant.now());
    }
}
