package com.subscriptio.domain.repository;

import com.subscriptio.domain.model.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanRepository {
    Plan save(Plan plan);
    Optional<Plan> findByExternalId(UUID externalId);
    Page<Plan> findByTenantId(Long tenantId, Pageable pageable);
    List<Plan> findActivePlansByTenantId(Long tenantId);
}
