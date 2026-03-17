package com.subscriptio.api.customer;

import com.subscriptio.api.dto.request.ChangePlanRequest;
import com.subscriptio.api.dto.request.CheckoutRequest;
import com.subscriptio.api.dto.response.SubscriptionResponse;
import com.subscriptio.application.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subscription")
public class CustomerSubscriptionController {

    private final SubscriptionService subscriptionService;

    public CustomerSubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<SubscriptionResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        SubscriptionResponse response = subscriptionService.create(
                request.tenantId(), request.customerId(), request.planId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionResponse> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.cancel(id));
    }

    @PostMapping("/{id}/change")
    public ResponseEntity<SubscriptionResponse> changePlan(@PathVariable UUID id,
                                                            @Valid @RequestBody ChangePlanRequest request) {
        return ResponseEntity.ok(subscriptionService.changePlan(id, request.newPlanId()));
    }
}
