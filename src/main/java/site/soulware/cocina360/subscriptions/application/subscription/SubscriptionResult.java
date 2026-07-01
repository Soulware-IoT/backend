package site.soulware.cocina360.subscriptions.application.subscription;

import site.soulware.cocina360.subscriptions.domain.model.aggregate.Subscription;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionResult(
        UUID id,
        UUID organizationId,
        UUID ownedBy,
        SubscriptionPlan plan,
        Instant currentPeriodEnd,
        boolean cancelAtPeriodEnd,
        Instant createdAt,
        Instant updatedAt
) {
    /** Plain view (no billing schedule) — for internal reads that must not call Stripe. */
    public static SubscriptionResult from(Subscription subscription) {
        return from(subscription, BillingGateway.BillingSchedule.none());
    }

    /** Billing-enriched view — {@code schedule} is fetched live from Stripe by the query service. */
    public static SubscriptionResult from(Subscription subscription, BillingGateway.BillingSchedule schedule) {
        return new SubscriptionResult(
                subscription.getId().value(),
                subscription.getOrganizationId().value(),
                subscription.getOwnedBy().value(),
                subscription.getPlan(),
                schedule.currentPeriodEnd(),
                schedule.cancelAtPeriodEnd(),
                subscription.getCreatedAt(),
                subscription.getUpdatedAt()
        );
    }
}
