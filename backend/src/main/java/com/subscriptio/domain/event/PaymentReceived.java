package com.subscriptio.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentReceived(
        UUID paymentId,
        UUID invoiceId,
        BigDecimal amount,
        Instant occurredAt
) implements DomainEvent {

    public PaymentReceived(UUID paymentId, UUID invoiceId, BigDecimal amount) {
        this(paymentId, invoiceId, amount, Instant.now());
    }
}
