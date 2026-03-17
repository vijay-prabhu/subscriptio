package com.subscriptio.domain.repository;

import com.subscriptio.domain.model.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByExternalId(UUID externalId);
}
