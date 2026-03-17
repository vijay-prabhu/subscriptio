package com.subscriptio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.stripe")
public record StripeProperties(
        String apiKey,
        String webhookSecret,
        String publishableKey
) {
}
