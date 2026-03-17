package com.subscriptio.infrastructure.persistence;

import com.subscriptio.domain.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataTenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByExternalId(UUID externalId);
    Optional<Tenant> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
