package com.subscriptio.application;

import com.subscriptio.api.dto.request.CreateCustomerRequest;
import com.subscriptio.api.dto.response.CustomerResponse;
import com.subscriptio.api.dto.response.PageResponse;
import com.subscriptio.domain.model.Customer;
import com.subscriptio.domain.model.Tenant;
import com.subscriptio.domain.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final TenantService tenantService;

    public CustomerService(CustomerRepository customerRepo, TenantService tenantService) {
        this.customerRepo = customerRepo;
        this.tenantService = tenantService;
    }

    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {
        Tenant tenant = tenantService.findEntityByExternalId(request.tenantId());
        if (customerRepo.findByTenantIdAndEmail(tenant.getId(), request.email()).isPresent()) {
            throw new IllegalArgumentException("Customer with email '" + request.email() + "' already exists for this tenant");
        }
        Customer customer = new Customer(tenant, request.email(), request.name());
        return CustomerResponse.from(customerRepo.save(customer));
    }

    @Transactional(readOnly = true)
    public CustomerResponse getByExternalId(UUID externalId) {
        return customerRepo.findByExternalId(externalId)
                .map(CustomerResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + externalId));
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> listByTenant(UUID tenantExternalId, Pageable pageable) {
        Tenant tenant = tenantService.findEntityByExternalId(tenantExternalId);
        return PageResponse.from(customerRepo.findByTenantId(tenant.getId(), pageable), CustomerResponse::from);
    }

    Customer findEntityByExternalId(UUID externalId) {
        return customerRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + externalId));
    }
}
