package com.subscriptio.application;

import com.subscriptio.api.dto.response.PageResponse;
import com.subscriptio.api.dto.response.SubscriptionResponse;
import com.subscriptio.domain.model.*;
import com.subscriptio.domain.repository.SubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepo;
    private final TenantService tenantService;
    private final CustomerService customerService;
    private final PlanService planService;

    public SubscriptionService(SubscriptionRepository subscriptionRepo, TenantService tenantService,
                               CustomerService customerService, PlanService planService) {
        this.subscriptionRepo = subscriptionRepo;
        this.tenantService = tenantService;
        this.customerService = customerService;
        this.planService = planService;
    }

    @Transactional
    public SubscriptionResponse create(UUID tenantId, UUID customerId, UUID planId) {
        Tenant tenant = tenantService.findEntityByExternalId(tenantId);
        Customer customer = customerService.findEntityByExternalId(customerId);
        Plan plan = planService.findEntityByExternalId(planId);

        // Check if customer already has an active subscription
        var activeStatuses = List.of(SubscriptionStatus.TRIALING, SubscriptionStatus.ACTIVE);
        if (subscriptionRepo.findByCustomerIdAndStatusIn(customer.getId(), activeStatuses).isPresent()) {
            throw new IllegalStateException("Customer already has an active subscription");
        }

        Instant now = Instant.now();
        Instant periodEnd;
        SubscriptionStatus initialStatus;

        if (plan.getTrialDays() > 0) {
            periodEnd = now.plus(plan.getTrialDays(), ChronoUnit.DAYS);
            initialStatus = SubscriptionStatus.TRIALING;
        } else {
            periodEnd = "yearly".equals(plan.getBillingInterval())
                    ? now.plus(365, ChronoUnit.DAYS)
                    : now.plus(30, ChronoUnit.DAYS);
            initialStatus = SubscriptionStatus.ACTIVE;
        }

        Subscription subscription = new Subscription(tenant, customer, plan, now, periodEnd);
        if (plan.getTrialDays() > 0) {
            subscription.setTrialEnd(periodEnd);
        }

        // If no trial, directly set to ACTIVE
        if (initialStatus == SubscriptionStatus.ACTIVE) {
            // Use reflection-free approach: create as TRIALING then activate
            // Actually, the constructor defaults to TRIALING, so we activate if no trial
            subscription.activate();
        }

        return SubscriptionResponse.from(subscriptionRepo.save(subscription));
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getByExternalId(UUID externalId) {
        return subscriptionRepo.findByExternalId(externalId)
                .map(SubscriptionResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + externalId));
    }

    @Transactional(readOnly = true)
    public PageResponse<SubscriptionResponse> listByTenant(UUID tenantExternalId, Pageable pageable) {
        Tenant tenant = tenantService.findEntityByExternalId(tenantExternalId);
        return PageResponse.from(subscriptionRepo.findByTenantId(tenant.getId(), pageable), SubscriptionResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<SubscriptionResponse> listByTenantAndStatus(UUID tenantExternalId,
                                                                     SubscriptionStatus status, Pageable pageable) {
        Tenant tenant = tenantService.findEntityByExternalId(tenantExternalId);
        return PageResponse.from(
                subscriptionRepo.findByTenantIdAndStatus(tenant.getId(), status, pageable),
                SubscriptionResponse::from
        );
    }

    @Transactional
    public SubscriptionResponse cancel(UUID externalId) {
        Subscription subscription = subscriptionRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + externalId));
        subscription.requestCancelAtPeriodEnd();
        return SubscriptionResponse.from(subscriptionRepo.save(subscription));
    }

    @Transactional
    public SubscriptionResponse changePlan(UUID subscriptionExternalId, UUID newPlanExternalId) {
        Subscription subscription = subscriptionRepo.findByExternalId(subscriptionExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + subscriptionExternalId));
        Plan newPlan = planService.findEntityByExternalId(newPlanExternalId);
        subscription.changePlan(newPlan);
        return SubscriptionResponse.from(subscriptionRepo.save(subscription));
    }
}
