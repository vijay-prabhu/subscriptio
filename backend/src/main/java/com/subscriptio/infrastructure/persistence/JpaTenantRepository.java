package com.subscriptio.infrastructure.persistence;

import com.subscriptio.domain.model.Tenant;
import com.subscriptio.domain.repository.TenantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTenantRepository implements TenantRepository {

    private final SpringDataTenantRepository springDataRepo;

    public JpaTenantRepository(SpringDataTenantRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public Tenant save(Tenant tenant) {
        return springDataRepo.save(tenant);
    }

    @Override
    public Optional<Tenant> findByExternalId(UUID externalId) {
        return springDataRepo.findByExternalId(externalId);
    }

    @Override
    public Optional<Tenant> findBySlug(String slug) {
        return springDataRepo.findBySlug(slug);
    }

    @Override
    public Page<Tenant> findAll(Pageable pageable) {
        return springDataRepo.findAll(pageable);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return springDataRepo.existsBySlug(slug);
    }
}
