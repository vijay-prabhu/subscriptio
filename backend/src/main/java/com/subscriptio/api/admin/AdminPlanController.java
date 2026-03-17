package com.subscriptio.api.admin;

import com.subscriptio.api.dto.request.CreatePlanRequest;
import com.subscriptio.api.dto.response.PageResponse;
import com.subscriptio.api.dto.response.PlanResponse;
import com.subscriptio.application.PlanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/plans")
public class AdminPlanController {

    private final PlanService planService;

    public AdminPlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping
    public ResponseEntity<PlanResponse> create(@Valid @RequestBody CreatePlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(planService.getByExternalId(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PlanResponse>> list(
            @RequestParam UUID tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(planService.listByTenant(tenantId, PageRequest.of(page, size)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody CreatePlanRequest request) {
        return ResponseEntity.ok(planService.update(id, request));
    }
}
