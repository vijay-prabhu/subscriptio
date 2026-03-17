package com.subscriptio.domain.repository;

import com.subscriptio.domain.model.Subscription;
import com.subscriptio.domain.model.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository {
    Subscription save(Subscription subscription);
    Optional<Subscription> findByExternalId(UUID externalId);
    Optional<Subscription> findByCustomerIdAndStatusIn(Long customerId, List<SubscriptionStatus> statuses);
    Page<Subscription> findByTenantId(Long tenantId, Pageable pageable);
    Page<Subscription> findByTenantIdAndStatus(Long tenantId, SubscriptionStatus status, Pageable pageable);
    List<Subscription> findDueForBilling(Instant before);
    long countByTenantIdAndStatus(Long tenantId, SubscriptionStatus status);
}
