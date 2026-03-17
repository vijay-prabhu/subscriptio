package com.subscriptio.infrastructure.persistence;

import com.subscriptio.domain.model.Subscription;
import com.subscriptio.domain.model.SubscriptionStatus;
import com.subscriptio.domain.repository.SubscriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSubscriptionRepository implements SubscriptionRepository {

    private final SpringDataSubscriptionRepository springDataRepo;

    public JpaSubscriptionRepository(SpringDataSubscriptionRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public Subscription save(Subscription subscription) {
        return springDataRepo.save(subscription);
    }

    @Override
    public Optional<Subscription> findByExternalId(UUID externalId) {
        return springDataRepo.findByExternalId(externalId);
    }

    @Override
    public Optional<Subscription> findByCustomerIdAndStatusIn(Long customerId, List<SubscriptionStatus> statuses) {
        return springDataRepo.findByCustomerIdAndStatusIn(customerId, statuses);
    }

    @Override
    public Page<Subscription> findByTenantId(Long tenantId, Pageable pageable) {
        return springDataRepo.findByTenantId(tenantId, pageable);
    }

    @Override
    public Page<Subscription> findByTenantIdAndStatus(Long tenantId, SubscriptionStatus status, Pageable pageable) {
        return springDataRepo.findByTenantIdAndStatus(tenantId, status, pageable);
    }

    @Override
    public List<Subscription> findDueForBilling(Instant before) {
        return springDataRepo.findDueForBilling(before);
    }

    @Override
    public long countByTenantIdAndStatus(Long tenantId, SubscriptionStatus status) {
        return springDataRepo.countByTenantIdAndStatus(tenantId, status);
    }
}
