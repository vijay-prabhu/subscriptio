package com.subscriptio.domain.repository;

import com.subscriptio.domain.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findByExternalId(UUID externalId);
    Optional<Customer> findByTenantIdAndEmail(Long tenantId, String email);
    Page<Customer> findByTenantId(Long tenantId, Pageable pageable);
}
