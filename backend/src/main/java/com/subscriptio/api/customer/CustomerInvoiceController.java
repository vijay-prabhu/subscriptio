package com.subscriptio.api.customer;

import com.subscriptio.api.dto.response.InvoiceResponse;
import com.subscriptio.api.dto.response.PageResponse;
import com.subscriptio.application.InvoiceService;
import com.subscriptio.infrastructure.security.TenantContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
public class CustomerInvoiceController {

    private final InvoiceService invoiceService;

    public CustomerInvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.getByExternalId(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<InvoiceResponse>> list(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        TenantContext ctx = (TenantContext) auth.getDetails();
        return ResponseEntity.ok(invoiceService.listByCustomer(ctx.tenantId(), PageRequest.of(page, size)));
    }
}
