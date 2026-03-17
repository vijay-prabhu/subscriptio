package com.subscriptio.domain.event;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredAt();
}
