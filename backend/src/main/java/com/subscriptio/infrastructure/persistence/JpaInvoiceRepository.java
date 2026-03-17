package com.subscriptio.infrastructure.persistence;

import com.subscriptio.domain.model.Invoice;
import com.subscriptio.domain.model.InvoiceStatus;
import com.subscriptio.domain.repository.InvoiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaInvoiceRepository implements InvoiceRepository {

    private final SpringDataInvoiceRepository springDataRepo;

    public JpaInvoiceRepository(SpringDataInvoiceRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public Invoice save(Invoice invoice) {
        return springDataRepo.save(invoice);
    }

    @Override
    public Optional<Invoice> findByExternalId(UUID externalId) {
        return springDataRepo.findByExternalId(externalId);
    }

    @Override
    public Page<Invoice> findByTenantId(Long tenantId, Pageable pageable) {
        return springDataRepo.findByTenantId(tenantId, pageable);
    }

    @Override
    public Page<Invoice> findByCustomerId(Long customerId, Pageable pageable) {
        return springDataRepo.findByCustomerId(customerId, pageable);
    }

    @Override
    public Page<Invoice> findByTenantIdAndStatus(Long tenantId, InvoiceStatus status, Pageable pageable) {
        return springDataRepo.findByTenantIdAndStatus(tenantId, status, pageable);
    }

    @Override
    public long countByTenantId(Long tenantId) {
        return springDataRepo.countByTenantId(tenantId);
    }
}
