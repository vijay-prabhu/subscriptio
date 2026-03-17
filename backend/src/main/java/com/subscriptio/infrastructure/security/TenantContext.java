package com.subscriptio.infrastructure.security;

public record TenantContext(Long tenantId, String email) {
}
