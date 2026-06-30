package site.soulware.cocina360.subscriptions.domain.model.aggregate;

import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;
import site.soulware.cocina360.subscriptions.domain.model.event.SubscriptionCreated;
import site.soulware.cocina360.subscriptions.domain.model.event.SubscriptionPlanChanged;
import site.soulware.cocina360.subscriptions.domain.model.event.SubscriptionStatusChanged;
import site.soulware.cocina360.subscriptions.domain.model.exception.CannotCancelSubscriptionException;
import site.soulware.cocina360.subscriptions.domain.model.exception.CannotReactivateSubscriptionException;
import site.soulware.cocina360.subscriptions.domain.model.exception.CannotSuspendSubscriptionException;
import site.soulware.cocina360.subscriptions.domain.model.exception.SubscriptionPlanUnchangedException;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionId;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionStatus;

import java.time.Instant;

public class Subscription extends AggregateRoot<SubscriptionId> {

    private final SubscriptionId id;
    private final OrganizationId organizationId;
    private final ProfileId ownedBy;
    private SubscriptionPlan plan;
    private SubscriptionStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Subscription(
        SubscriptionId id,
        OrganizationId organizationId,
        ProfileId ownedBy,
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

    public static Subscription create(
        SubscriptionId id,
        OrganizationId organizationId,
        ProfileId ownedBy,
        SubscriptionPlan plan
    ) {
        Instant now = Instant.now();
        Subscription subscription = new Subscription(
                id, organizationId, ownedBy, plan, SubscriptionStatus.ACTIVE, now, now);
        subscription.registerEvent(new SubscriptionCreated(
                id.value(), organizationId.value(), ownedBy.value(), plan, now));
        return subscription;
    }

    public static Subscription rehydrate(
        SubscriptionId id,
        OrganizationId organizationId,
        ProfileId ownedBy,
        SubscriptionPlan plan,
        SubscriptionStatus status,
        Instant createdAt,
        Instant updatedAt
    ) {
        return new Subscription(id, organizationId, ownedBy, plan, status, createdAt, updatedAt);
    }

    public void changePlan(SubscriptionPlan newPlan) {
        if (this.plan == newPlan) throw new SubscriptionPlanUnchangedException(newPlan);
        SubscriptionPlan previous = this.plan;
        this.plan = newPlan;
        this.updatedAt = Instant.now();
        this.registerEvent(new SubscriptionPlanChanged(
                this.id.value(), this.organizationId.value(), previous, newPlan, this.updatedAt));
    }

    public void suspend() {
        if (!this.status.canSuspend()) throw new CannotSuspendSubscriptionException(this.status);
        this.transition(SubscriptionStatus.SUSPENDED);
    }

    public void cancel() {
        if (!this.status.canCancel()) throw new CannotCancelSubscriptionException(this.status);
        this.transition(SubscriptionStatus.CANCELLED);
    }

    public void reactivate() {
        if (!this.status.canReactivate()) throw new CannotReactivateSubscriptionException(this.status);
        this.transition(SubscriptionStatus.ACTIVE);
    }

    private void transition(SubscriptionStatus newStatus) {
        SubscriptionStatus previous = this.status;
        this.status = newStatus;
        this.updatedAt = Instant.now();
        this.registerEvent(new SubscriptionStatusChanged(
                this.id.value(), this.organizationId.value(), previous, newStatus, this.updatedAt));
    }

    @Override
    public SubscriptionId getId() { return this.id; }
    public OrganizationId getOrganizationId() { return this.organizationId; }
    public ProfileId getOwnedBy() { return this.ownedBy; }
    public SubscriptionPlan getPlan() { return this.plan; }
    public SubscriptionStatus getStatus() { return this.status; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }
}
