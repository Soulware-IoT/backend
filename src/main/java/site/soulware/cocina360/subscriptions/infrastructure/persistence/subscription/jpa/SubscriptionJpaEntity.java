package site.soulware.cocina360.subscriptions.infrastructure.persistence.subscription.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class SubscriptionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false, unique = true, updatable = false)
    private UUID organizationId;

    @Column(name = "owned_by", nullable = false)
    private UUID ownedBy;

    @Column(nullable = false)
    @ColumnTransformer(write = "?::subscription_plan")
    private SubscriptionPlan plan;

    @Column(nullable = false)
    @ColumnTransformer(write = "?::subscription_status")
    private SubscriptionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SubscriptionJpaEntity() {}

    public SubscriptionJpaEntity(
        UUID id,
        UUID organizationId,
        UUID ownedBy,
        SubscriptionPlan plan,
        SubscriptionStatus status,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.ownedBy = ownedBy;
        this.plan = plan;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return this.id; }
    public UUID getOrganizationId() { return this.organizationId; }
    public UUID getOwnedBy() { return this.ownedBy; }
    public SubscriptionPlan getPlan() { return this.plan; }
    public SubscriptionStatus getStatus() { return this.status; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }
}
