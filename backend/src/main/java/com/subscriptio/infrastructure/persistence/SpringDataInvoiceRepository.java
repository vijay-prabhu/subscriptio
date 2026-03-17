package com.subscriptio.infrastructure.persistence;

import com.subscriptio.domain.model.Invoice;
import com.subscriptio.domain.model.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataInvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByExternalId(UUID externalId);
    Page<Invoice> findByTenantId(Long tenantId, Pageable pageable);
    Page<Invoice> findByCustomerId(Long customerId, Pageable pageable);
    Page<Invoice> findByTenantIdAndStatus(Long tenantId, InvoiceStatus status, Pageable pageable);
    long countByTenantId(Long tenantId);
}
