package com.subscriptio.infrastructure.persistence;

import com.subscriptio.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataPaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByExternalId(UUID externalId);
}
