package com.subscriptio.infrastructure.persistence;

import com.subscriptio.domain.model.Customer;
import com.subscriptio.domain.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaCustomerRepository implements CustomerRepository {

    private final SpringDataCustomerRepository springDataRepo;

    public JpaCustomerRepository(SpringDataCustomerRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public Customer save(Customer customer) {
        return springDataRepo.save(customer);
    }

    @Override
    public Optional<Customer> findByExternalId(UUID externalId) {
        return springDataRepo.findByExternalId(externalId);
    }

    @Override
    public Optional<Customer> findByTenantIdAndEmail(Long tenantId, String email) {
        return springDataRepo.findByTenantIdAndEmail(tenantId, email);
    }

    @Override
    public Page<Customer> findByTenantId(Long tenantId, Pageable pageable) {
        return springDataRepo.findByTenantId(tenantId, pageable);
    }
}
