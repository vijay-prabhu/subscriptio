package com.subscriptio.domain.repository;

import com.subscriptio.domain.model.Invoice;
import com.subscriptio.domain.model.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {
    Invoice save(Invoice invoice);
    Optional<Invoice> findByExternalId(UUID externalId);
    Page<Invoice> findByTenantId(Long tenantId, Pageable pageable);
    Page<Invoice> findByCustomerId(Long customerId, Pageable pageable);
    Page<Invoice> findByTenantIdAndStatus(Long tenantId, InvoiceStatus status, Pageable pageable);
    long countByTenantId(Long tenantId);
}
