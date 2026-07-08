package site.soulware.cocina360.subscriptions.application.subscription;

import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Outbound port to the external billing provider (Stripe). Called synchronously from
 * {@link SubscriptionCommandService} so that billing calls and the domain mutation succeed or
 * fail together within the same transaction.
 */
public interface BillingGateway {

    BillingIds activate(UUID organizationId, String paymentMethodId, SubscriptionPlan plan);

    /**
     * Immediately switches the subscription up to {@code newPlan} and invoices the prorated difference
     * right away (Stripe {@code always_invoice}). Releases any pending downgrade schedule/cancellation
     * first, so an upgrade always supersedes a scheduled downgrade. Use only for upgrades.
     */
    void upgradePlan(String stripeSubscriptionId, SubscriptionPlan newPlan);

    /**
     * Schedules a downgrade that takes effect at the end of the current paid period — the plan stays
     * on its current (higher) tier until then. To {@code FREE} this is Stripe's
     * {@code cancel_at_period_end} (the {@code customer.subscription.deleted} webhook then drops the org
     * to FREE); to a cheaper paid plan it is a Stripe subscription schedule whose next phase switches
     * the price at period end (the {@code customer.subscription.updated} webhook then syncs the plan).
     */
    void scheduleDowngrade(String stripeSubscriptionId, SubscriptionPlan target);

    /** Cancels any pending end-of-period downgrade (cancellation or schedule) — the subscription keeps renewing on its current plan. */
    void resume(String stripeSubscriptionId);

    /** Reads the subscription's live billing schedule from Stripe (period end + pending plan change). */
    BillingSchedule fetchSchedule(String stripeSubscriptionId);

    /** Reads the customer's invoice history from Stripe (most recent first). */
    List<InvoiceView> listInvoices(String stripeCustomerId);

    record BillingIds(String customerId, String subscriptionId) {}

    /**
     * A single Stripe invoice, flattened for read-only display. {@code amountPaid} is in the currency's
     * minor units (e.g. cents). {@code hostedInvoiceUrl}/{@code invoicePdfUrl} may be null for draft invoices.
     */
    record InvoiceView(
            String number,
            String status,
            long amountPaid,
            String currency,
            Instant createdAt,
            String hostedInvoiceUrl,
            String invoicePdfUrl
    ) {}

    /**
     * When the current paid period ends and, if a downgrade is scheduled, the plan it will drop to at
     * that point ({@code pendingPlan}). {@code pendingPlan} is {@code null} when no change is scheduled;
     * it may be {@code FREE} (pending cancellation) or a cheaper paid plan (pending schedule). For an org
     * on FREE there is no schedule — use {@link #none()}.
     */
    record BillingSchedule(Instant currentPeriodEnd, SubscriptionPlan pendingPlan) {
        public static BillingSchedule none() {
            return new BillingSchedule(null, null);
        }
    }
}
