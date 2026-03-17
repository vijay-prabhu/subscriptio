package com.subscriptio.api.dto.response;

import com.subscriptio.domain.model.Customer;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String email,
        String name,
        Instant createdAt
) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getExternalId(),
                customer.getEmail(),
                customer.getName(),
                customer.getCreatedAt()
        );
    }
}
