package com.subscriptio.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTenantRequest(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 100) String slug
) {
}
