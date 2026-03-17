package com.subscriptio.api.admin;

import com.subscriptio.api.dto.response.InvoiceResponse;
import com.subscriptio.api.dto.response.PageResponse;
import com.subscriptio.application.InvoiceService;
import com.subscriptio.domain.model.InvoiceStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/invoices")
public class AdminInvoiceController {

    private final InvoiceService invoiceService;

    public AdminInvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.getByExternalId(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<InvoiceResponse>> list(
            @RequestParam UUID tenantId,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (status != null) {
            return ResponseEntity.ok(invoiceService.listByTenantAndStatus(tenantId, status, PageRequest.of(page, size)));
        }
        return ResponseEntity.ok(invoiceService.listByTenant(tenantId, PageRequest.of(page, size)));
    }
}
