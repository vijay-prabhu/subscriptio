package com.subscriptio.infrastructure.persistence;

import com.subscriptio.domain.model.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataPlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByExternalId(UUID externalId);
    Page<Plan> findByTenantId(Long tenantId, Pageable pageable);
    List<Plan> findByTenantIdAndActiveTrue(Long tenantId);
}
