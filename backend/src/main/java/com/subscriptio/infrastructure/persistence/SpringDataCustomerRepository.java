package com.subscriptio.infrastructure.persistence;

import com.subscriptio.domain.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataCustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByExternalId(UUID externalId);
    Optional<Customer> findByTenantIdAndEmail(Long tenantId, String email);
    Page<Customer> findByTenantId(Long tenantId, Pageable pageable);
}
