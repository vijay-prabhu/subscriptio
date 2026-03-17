package com.subscriptio.api.admin;

import com.subscriptio.api.dto.response.PageResponse;
import com.subscriptio.api.dto.response.SubscriptionResponse;
import com.subscriptio.application.SubscriptionService;
import com.subscriptio.domain.model.SubscriptionStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/subscriptions")
public class AdminSubscriptionController {

    private final SubscriptionService subscriptionService;

    public AdminSubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.getByExternalId(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<SubscriptionResponse>> list(
            @RequestParam UUID tenantId,
            @RequestParam(required = false) SubscriptionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (status != null) {
            return ResponseEntity.ok(subscriptionService.listByTenantAndStatus(tenantId, status, PageRequest.of(page, size)));
        }
        return ResponseEntity.ok(subscriptionService.listByTenant(tenantId, PageRequest.of(page, size)));
    }
}
