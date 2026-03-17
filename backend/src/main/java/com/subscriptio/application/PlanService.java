package com.subscriptio.application;

import com.subscriptio.api.dto.request.CreatePlanRequest;
import com.subscriptio.api.dto.response.PageResponse;
import com.subscriptio.api.dto.response.PlanResponse;
import com.subscriptio.domain.model.Plan;
import com.subscriptio.domain.model.Tenant;
import com.subscriptio.domain.repository.PlanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PlanService {

    private final PlanRepository planRepo;
    private final TenantService tenantService;

    public PlanService(PlanRepository planRepo, TenantService tenantService) {
        this.planRepo = planRepo;
        this.tenantService = tenantService;
    }

    @Transactional
    public PlanResponse create(CreatePlanRequest request) {
        Tenant tenant = tenantService.findEntityByExternalId(request.tenantId());
        Plan plan = new Plan(tenant, request.name(), request.price(), request.billingInterval());
        plan.setDescription(request.description());
        plan.setCurrency(request.currency());
        plan.setTrialDays(request.trialDays());
        if (request.features() != null) {
            plan.setFeatures(String.join(",", request.features()));
        }
        return PlanResponse.from(planRepo.save(plan));
    }

    @Transactional(readOnly = true)
    public PlanResponse getByExternalId(UUID externalId) {
        return planRepo.findByExternalId(externalId)
                .map(PlanResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found: " + externalId));
    }

    @Transactional(readOnly = true)
    public PageResponse<PlanResponse> listByTenant(UUID tenantExternalId, Pageable pageable) {
        Tenant tenant = tenantService.findEntityByExternalId(tenantExternalId);
        return PageResponse.from(planRepo.findByTenantId(tenant.getId(), pageable), PlanResponse::from);
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> listActivePlans(UUID tenantExternalId) {
        Tenant tenant = tenantService.findEntityByExternalId(tenantExternalId);
        return planRepo.findActivePlansByTenantId(tenant.getId()).stream()
                .map(PlanResponse::from)
                .toList();
    }

    @Transactional
    public PlanResponse update(UUID externalId, CreatePlanRequest request) {
        Plan plan = planRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found: " + externalId));
        plan.setName(request.name());
        plan.setDescription(request.description());
        plan.setPrice(request.price());
        plan.setTrialDays(request.trialDays());
        if (request.features() != null) {
            plan.setFeatures(String.join(",", request.features()));
        }
        return PlanResponse.from(planRepo.save(plan));
    }

    Plan findEntityByExternalId(UUID externalId) {
        return planRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found: " + externalId));
    }
}
