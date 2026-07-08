package site.soulware.cocina360.subscriptions.domain.model.aggregate;

import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;
import site.soulware.cocina360.subscriptions.domain.model.event.SubscriptionCreated;
import site.soulware.cocina360.subscriptions.domain.model.event.SubscriptionPlanChanged;
import site.soulware.cocina360.subscriptions.domain.model.exception.SubscriptionPlanUnchangedException;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionId;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

import java.time.Instant;

/**
 * A subscription is permanent and 1:1 with an organization. Its only axis is the {@link SubscriptionPlan}:
 * an org always has a subscription, on FREE or a paid plan. There is no cancellation or suspension —
 * leaving a paid plan (voluntarily, or after Stripe exhausts payment retries) is a downgrade to FREE.
 */
public class Subscription extends AggregateRoot<SubscriptionId> {

    private final SubscriptionId id;
    private final OrganizationId organizationId;
    private final ProfileId ownedBy;
    private SubscriptionPlan plan;
    private final Instant createdAt;
    private Instant updatedAt;
    private String stripeCustomerId;
    private String stripeSubscriptionId;

    private Subscription(
        SubscriptionId id,
        OrganizationId organizationId,
        ProfileId ownedBy,
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

    public static Subscription create(
        SubscriptionId id,
        OrganizationId organizationId,
        ProfileId ownedBy,
        SubscriptionPlan plan
    ) {
        Instant now = Instant.now();
        Subscription subscription = new Subscription(id, organizationId, ownedBy, plan, now, now, null, null);
        subscription.registerEvent(new SubscriptionCreated(
                id.value(), organizationId.value(), ownedBy.value(), plan, now));
        return subscription;
    }

    public static Subscription rehydrate(
        SubscriptionId id,
        OrganizationId organizationId,
        ProfileId ownedBy,
        SubscriptionPlan plan,
        Instant createdAt,
        Instant updatedAt,
        String stripeCustomerId,
        String stripeSubscriptionId
    ) {
        return new Subscription(
                id, organizationId, ownedBy, plan, createdAt, updatedAt,
                stripeCustomerId, stripeSubscriptionId);
    }

    public void changePlan(SubscriptionPlan newPlan) {
        if (this.plan == newPlan) throw new SubscriptionPlanUnchangedException(newPlan);
        SubscriptionPlan previous = this.plan;
        this.plan = newPlan;
        this.updatedAt = Instant.now();
        this.registerEvent(new SubscriptionPlanChanged(
                this.id.value(), this.organizationId.value(), previous, newPlan, this.updatedAt));
    }

    public void attachBillingIds(String customerId, String subscriptionId) {
        this.stripeCustomerId = customerId;
        this.stripeSubscriptionId = subscriptionId;
        this.updatedAt = Instant.now();
    }

    public void detachBillingIds() {
        this.stripeCustomerId = null;
        this.stripeSubscriptionId = null;
        this.updatedAt = Instant.now();
    }

    @Override
    public SubscriptionId getId() { return this.id; }
    public OrganizationId getOrganizationId() { return this.organizationId; }
    public ProfileId getOwnedBy() { return this.ownedBy; }
    public SubscriptionPlan getPlan() { return this.plan; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }
    public String getStripeCustomerId() { return this.stripeCustomerId; }
    public String getStripeSubscriptionId() { return this.stripeSubscriptionId; }
}
