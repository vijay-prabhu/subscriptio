package com.subscriptio.application;

import com.subscriptio.api.dto.response.DashboardMetricsResponse;
import com.subscriptio.domain.model.Plan;
import com.subscriptio.domain.model.SubscriptionStatus;
import com.subscriptio.domain.model.Tenant;
import com.subscriptio.domain.repository.InvoiceRepository;
import com.subscriptio.domain.repository.PlanRepository;
import com.subscriptio.domain.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MetricsService {

    private final SubscriptionRepository subscriptionRepo;
    private final InvoiceRepository invoiceRepo;
    private final PlanRepository planRepo;
    private final TenantService tenantService;

    public MetricsService(SubscriptionRepository subscriptionRepo, InvoiceRepository invoiceRepo,
                          PlanRepository planRepo, TenantService tenantService) {
        this.subscriptionRepo = subscriptionRepo;
        this.invoiceRepo = invoiceRepo;
        this.planRepo = planRepo;
        this.tenantService = tenantService;
    }

    @Transactional(readOnly = true)
    public DashboardMetricsResponse getDashboardMetrics(UUID tenantExternalId) {
        Tenant tenant = tenantService.findEntityByExternalId(tenantExternalId);
        Long tenantId = tenant.getId();

        long active = subscriptionRepo.countByTenantIdAndStatus(tenantId, SubscriptionStatus.ACTIVE);
        long trialing = subscriptionRepo.countByTenantIdAndStatus(tenantId, SubscriptionStatus.TRIALING);
        long pastDue = subscriptionRepo.countByTenantIdAndStatus(tenantId, SubscriptionStatus.PAST_DUE);
        long totalInvoices = invoiceRepo.countByTenantId(tenantId);

        // Calculate MRR from active plans
        List<Plan> activePlans = planRepo.findActivePlansByTenantId(tenantId);
        List<DashboardMetricsResponse.PlanBreakdown> breakdowns = new ArrayList<>();
        BigDecimal mrr = BigDecimal.ZERO;

        for (Plan plan : activePlans) {
            long subCount = subscriptionRepo.countByTenantIdAndStatus(tenantId, SubscriptionStatus.ACTIVE);
            BigDecimal planMrr = plan.getMonthlyPrice().multiply(BigDecimal.valueOf(subCount));
            mrr = mrr.add(planMrr);
            if (subCount > 0) {
                breakdowns.add(new DashboardMetricsResponse.PlanBreakdown(
                        plan.getName(), planMrr, subCount));
            }
        }

        return new DashboardMetricsResponse(mrr, active, trialing, pastDue, totalInvoices, breakdowns);
    }
}
