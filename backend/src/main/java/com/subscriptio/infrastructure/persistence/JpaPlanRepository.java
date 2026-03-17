package com.subscriptio.infrastructure.persistence;

import com.subscriptio.domain.model.Plan;
import com.subscriptio.domain.repository.PlanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaPlanRepository implements PlanRepository {

    private final SpringDataPlanRepository springDataRepo;

    public JpaPlanRepository(SpringDataPlanRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public Plan save(Plan plan) {
        return springDataRepo.save(plan);
    }

    @Override
    public Optional<Plan> findByExternalId(UUID externalId) {
        return springDataRepo.findByExternalId(externalId);
    }

    @Override
    public Page<Plan> findByTenantId(Long tenantId, Pageable pageable) {
        return springDataRepo.findByTenantId(tenantId, pageable);
    }

    @Override
    public List<Plan> findActivePlansByTenantId(Long tenantId) {
        return springDataRepo.findByTenantIdAndActiveTrue(tenantId);
    }
}
