package com.subscriptio.infrastructure.persistence;

import com.subscriptio.domain.model.Payment;
import com.subscriptio.domain.repository.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaPaymentRepository implements PaymentRepository {

    private final SpringDataPaymentRepository springDataRepo;

    public JpaPaymentRepository(SpringDataPaymentRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public Payment save(Payment payment) {
        return springDataRepo.save(payment);
    }

    @Override
    public Optional<Payment> findByExternalId(UUID externalId) {
        return springDataRepo.findByExternalId(externalId);
    }
}
