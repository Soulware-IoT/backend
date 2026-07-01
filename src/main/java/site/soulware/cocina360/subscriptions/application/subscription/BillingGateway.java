package site.soulware.cocina360.subscriptions.application.subscription;

import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

import java.time.Instant;
import java.util.UUID;

/**
 * Outbound port to the external billing provider (Stripe). Called synchronously from
 * {@link SubscriptionCommandService} so that billing calls and the domain mutation succeed or
 * fail together within the same transaction.
 */
public interface BillingGateway {

    BillingIds activate(UUID organizationId, String paymentMethodId, SubscriptionPlan plan);

    /** Switches the subscription's price and clears any pending end-of-period cancellation. */
    void updatePlan(String stripeSubscriptionId, SubscriptionPlan newPlan);

    /**
     * Schedules the subscription to cancel at the end of the current paid period (Stripe's
     * {@code cancel_at_period_end}). The plan stays paid until then; when the period ends Stripe
     * cancels it and emits {@code customer.subscription.deleted}, which drops the org to FREE.
     */
    void scheduleDowngrade(String stripeSubscriptionId);

    /** Cancels a pending end-of-period downgrade — the subscription keeps renewing. */
    void resume(String stripeSubscriptionId);

    /** Reads the subscription's live billing schedule from Stripe (period end + pending cancellation). */
    BillingSchedule fetchSchedule(String stripeSubscriptionId);

    record BillingIds(String customerId, String subscriptionId) {}

    /**
     * When the current paid period ends and whether the subscription is set to drop to FREE at that
     * point ({@code cancelAtPeriodEnd}). For an org on FREE there is no schedule — use {@link #none()}.
     */
    record BillingSchedule(Instant currentPeriodEnd, boolean cancelAtPeriodEnd) {
        public static BillingSchedule none() {
            return new BillingSchedule(null, false);
        }
    }
}
