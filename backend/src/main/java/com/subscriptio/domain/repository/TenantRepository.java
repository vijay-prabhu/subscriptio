package com.subscriptio.domain.repository;

import com.subscriptio.domain.model.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {
    Tenant save(Tenant tenant);
    Optional<Tenant> findByExternalId(UUID externalId);
    Optional<Tenant> findBySlug(String slug);
    Page<Tenant> findAll(Pageable pageable);
    boolean existsBySlug(String slug);
}
