package com.subscriptio.api.admin;

import com.subscriptio.api.dto.response.DashboardMetricsResponse;
import com.subscriptio.application.MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/metrics")
public class AdminMetricsController {

    private final MetricsService metricsService;

    public AdminMetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardMetricsResponse> dashboard(@RequestParam UUID tenantId) {
        return ResponseEntity.ok(metricsService.getDashboardMetrics(tenantId));
    }
}
