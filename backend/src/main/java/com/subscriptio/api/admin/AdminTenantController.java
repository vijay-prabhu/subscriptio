package com.subscriptio.api.admin;

import com.subscriptio.api.dto.request.CreateTenantRequest;
import com.subscriptio.api.dto.response.TenantResponse;
import com.subscriptio.application.TenantService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/tenants")
public class AdminTenantController {

    private final TenantService tenantService;

    public AdminTenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    public ResponseEntity<TenantResponse> create(@Valid @RequestBody CreateTenantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.getByExternalId(id));
    }

    @GetMapping
    public ResponseEntity<Page<TenantResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tenantService.list(PageRequest.of(page, size)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody CreateTenantRequest request) {
        return ResponseEntity.ok(tenantService.update(id, request));
    }
}
