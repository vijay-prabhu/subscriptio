package com.subscriptio.infrastructure.persistence;

import com.subscriptio.domain.model.Subscription;
import com.subscriptio.domain.model.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataSubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByExternalId(UUID externalId);

    Optional<Subscription> findByCustomerIdAndStatusIn(Long customerId, List<SubscriptionStatus> statuses);

    Page<Subscription> findByTenantId(Long tenantId, Pageable pageable);

    Page<Subscription> findByTenantIdAndStatus(Long tenantId, SubscriptionStatus status, Pageable pageable);

    @Query("SELECT s FROM Subscription s JOIN FETCH s.plan JOIN FETCH s.customer " +
            "WHERE s.currentPeriodEnd <= :before AND s.status IN ('ACTIVE', 'TRIALING')")
    List<Subscription> findDueForBilling(@Param("before") Instant before);

    long countByTenantIdAndStatus(Long tenantId, SubscriptionStatus status);
}
