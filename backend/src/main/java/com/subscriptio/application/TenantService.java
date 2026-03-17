package com.subscriptio.application;

import com.subscriptio.api.dto.request.CreateTenantRequest;
import com.subscriptio.api.dto.response.TenantResponse;
import com.subscriptio.domain.model.Tenant;
import com.subscriptio.domain.repository.TenantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TenantService {

    private final TenantRepository tenantRepo;

    public TenantService(TenantRepository tenantRepo) {
        this.tenantRepo = tenantRepo;
    }

    @Transactional
    public TenantResponse create(CreateTenantRequest request) {
        if (tenantRepo.existsBySlug(request.slug())) {
            throw new IllegalArgumentException("Tenant with slug '" + request.slug() + "' already exists");
        }
        Tenant tenant = new Tenant(request.name(), request.slug());
        return TenantResponse.from(tenantRepo.save(tenant));
    }

    @Transactional(readOnly = true)
    public TenantResponse getByExternalId(UUID externalId) {
        return tenantRepo.findByExternalId(externalId)
                .map(TenantResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found: " + externalId));
    }

    @Transactional(readOnly = true)
    public Page<TenantResponse> list(Pageable pageable) {
        return tenantRepo.findAll(pageable).map(TenantResponse::from);
    }

    @Transactional
    public TenantResponse update(UUID externalId, CreateTenantRequest request) {
        Tenant tenant = tenantRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found: " + externalId));
        tenant.setName(request.name());
        return TenantResponse.from(tenantRepo.save(tenant));
    }

    Tenant findEntityByExternalId(UUID externalId) {
        return tenantRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found: " + externalId));
    }
}
