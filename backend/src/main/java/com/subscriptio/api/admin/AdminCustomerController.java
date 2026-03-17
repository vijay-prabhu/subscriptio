package com.subscriptio.api.admin;

import com.subscriptio.api.dto.request.CreateCustomerRequest;
import com.subscriptio.api.dto.response.CustomerResponse;
import com.subscriptio.api.dto.response.PageResponse;
import com.subscriptio.application.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/customers")
public class AdminCustomerController {

    private final CustomerService customerService;

    public AdminCustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getByExternalId(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CustomerResponse>> list(
            @RequestParam UUID tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(customerService.listByTenant(tenantId, PageRequest.of(page, size)));
    }
}
