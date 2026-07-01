package site.soulware.cocina360.subscriptions.infrastructure.persistence.subscription.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;

    protected SubscriptionJpaEntity() {}

    public SubscriptionJpaEntity(
        UUID id,
        UUID organizationId,
        UUID ownedBy,
        SubscriptionPlan plan,
        Instant createdAt,
        Instant updatedAt,
        String stripeCustomerId,
        String stripeSubscriptionId
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.ownedBy = ownedBy;
        this.plan = plan;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stripeCustomerId = stripeCustomerId;
        this.stripeSubscriptionId = stripeSubscriptionId;
    }

    public UUID getId() { return this.id; }
    public UUID getOrganizationId() { return this.organizationId; }
    public UUID getOwnedBy() { return this.ownedBy; }
    public SubscriptionPlan getPlan() { return this.plan; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }
    public String getStripeCustomerId() { return this.stripeCustomerId; }
    public String getStripeSubscriptionId() { return this.stripeSubscriptionId; }
}
