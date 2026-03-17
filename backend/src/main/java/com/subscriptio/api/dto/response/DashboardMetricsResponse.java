package com.subscriptio.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardMetricsResponse(
        BigDecimal mrr,
        long activeSubscriptions,
        long trialingSubscriptions,
        long pastDueSubscriptions,
        long totalInvoices,
        List<PlanBreakdown> revenueByPlan
) {
    public record PlanBreakdown(
            String plan,
            BigDecimal revenue,
            long count
    ) {
    }
}
