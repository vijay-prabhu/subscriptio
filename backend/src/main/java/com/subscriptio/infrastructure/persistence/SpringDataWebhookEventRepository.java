package com.subscriptio.infrastructure.persistence;

import com.subscriptio.domain.model.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataWebhookEventRepository extends JpaRepository<WebhookEvent, Long> {
    Optional<WebhookEvent> findByStripeEventId(String stripeEventId);
}
