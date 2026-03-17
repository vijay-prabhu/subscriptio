package com.subscriptio.domain.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String actor;

    @Column(columnDefinition = "JSONB")
    private String changes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AuditLog() {
    }

    public AuditLog(Tenant tenant, String entityType, Long entityId, String action, String actor) {
        this.tenant = tenant;
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.actor = actor;
    }

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }
    public String getAction() { return action; }
    public String getActor() { return actor; }
    public String getChanges() { return changes; }
    public void setChanges(String changes) { this.changes = changes; }
    public Instant getCreatedAt() { return createdAt; }
}
