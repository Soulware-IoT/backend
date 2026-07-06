package site.soulware.cocina360.subscriptions.infrastructure.external.stripe.billing;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Invoice;
import com.stripe.model.PaymentMethod;
import com.stripe.model.SubscriptionSchedule;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.InvoiceListParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionScheduleCreateParams;
import com.stripe.param.SubscriptionScheduleUpdateParams;
import com.stripe.param.SubscriptionUpdateParams;
import org.springframework.stereotype.Service;
import site.soulware.cocina360.subscriptions.application.subscription.BillingGateway;
import site.soulware.cocina360.subscriptions.domain.model.exception.BillingActivationFailedException;
import site.soulware.cocina360.subscriptions.domain.model.exception.InvoiceRetrievalFailedException;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
class StripeBillingGateway implements BillingGateway {

    private final StripePriceResolver priceResolver;

    StripeBillingGateway(StripePriceResolver priceResolver) {
        this.priceResolver = priceResolver;
    }

    @Override
    public BillingIds activate(UUID organizationId, String paymentMethodId, SubscriptionPlan plan) {
        try {
            Customer customer = Customer.create(CustomerCreateParams.builder()
                    .putMetadata("organizationId", organizationId.toString())
                    .build());

            PaymentMethod attachedPm = PaymentMethod.retrieve(paymentMethodId).attach(
                    PaymentMethodAttachParams.builder().setCustomer(customer.getId()).build());

            customer.update(CustomerUpdateParams.builder()
                    .setInvoiceSettings(CustomerUpdateParams.InvoiceSettings.builder()
                            .setDefaultPaymentMethod(attachedPm.getId())
                            .build())
                    .build());

            com.stripe.model.Subscription stripeSubscription = com.stripe.model.Subscription.create(
                    SubscriptionCreateParams.builder()
                            .setCustomer(customer.getId())
                            .addItem(SubscriptionCreateParams.Item.builder()
                                    .setPrice(this.priceResolver.priceIdFor(plan))
                                    .build())
                            .build());

            return new BillingIds(customer.getId(), stripeSubscription.getId());
        } catch (StripeException e) {
            throw new BillingActivationFailedException(e.getMessage());
        }
    }

    @Override
    public void upgradePlan(String stripeSubscriptionId, SubscriptionPlan newPlan) {
        try {
            com.stripe.model.Subscription subscription =
                    com.stripe.model.Subscription.retrieve(stripeSubscriptionId);
            // An upgrade supersedes any pending downgrade: release a paid-downgrade schedule so the
            // subscription's items are directly editable again, then clear a pending cancellation below.
            if (subscription.getSchedule() != null) {
                SubscriptionSchedule.retrieve(subscription.getSchedule()).release();
                subscription = com.stripe.model.Subscription.retrieve(stripeSubscriptionId);
            }
            String itemId = subscription.getItems().getData().getFirst().getId();

            subscription.update(SubscriptionUpdateParams.builder()
                    .addItem(SubscriptionUpdateParams.Item.builder()
                            .setId(itemId)
                            .setPrice(this.priceResolver.priceIdFor(newPlan))
                            .build())
                    .setCancelAtPeriodEnd(false)
                    // Invoice the prorated upgrade amount immediately instead of deferring it to the next cycle.
                    .setProrationBehavior(SubscriptionUpdateParams.ProrationBehavior.ALWAYS_INVOICE)
                    .build());
        } catch (StripeException e) {
            throw new BillingActivationFailedException(e.getMessage());
        }
    }

    @Override
    public void scheduleDowngrade(String stripeSubscriptionId, SubscriptionPlan target) {
        try {
            if (target == SubscriptionPlan.FREE) {
                this.scheduleCancellation(stripeSubscriptionId);
            } else {
                this.schedulePaidDowngrade(stripeSubscriptionId, target);
            }
        } catch (StripeException e) {
            throw new BillingActivationFailedException(e.getMessage());
        }
    }

    /** Downgrade to FREE: cancel at period end. Drops any paid-downgrade schedule first (mutually exclusive). */
    private void scheduleCancellation(String stripeSubscriptionId) throws StripeException {
        com.stripe.model.Subscription subscription =
                com.stripe.model.Subscription.retrieve(stripeSubscriptionId);
        if (subscription.getSchedule() != null) {
            SubscriptionSchedule.retrieve(subscription.getSchedule()).release();
            subscription = com.stripe.model.Subscription.retrieve(stripeSubscriptionId);
        }
        subscription.update(SubscriptionUpdateParams.builder().setCancelAtPeriodEnd(true).build());
    }

    /**
     * Downgrade to a cheaper paid plan at period end via a subscription schedule: phase 1 keeps the
     * current price until the period ends, phase 2 switches to the target price with no proration. The
     * schedule releases after one target cycle, so the subscription then renews normally on the new plan.
     */
    private void schedulePaidDowngrade(String stripeSubscriptionId, SubscriptionPlan target) throws StripeException {
        com.stripe.model.Subscription subscription =
                com.stripe.model.Subscription.retrieve(stripeSubscriptionId);
        if (Boolean.TRUE.equals(subscription.getCancelAtPeriodEnd())) {
            subscription.update(SubscriptionUpdateParams.builder().setCancelAtPeriodEnd(false).build());
        }

        SubscriptionSchedule schedule = subscription.getSchedule() != null
                ? SubscriptionSchedule.retrieve(subscription.getSchedule())
                : SubscriptionSchedule.create(SubscriptionScheduleCreateParams.builder()
                        .setFromSubscription(stripeSubscriptionId)
                        .build());

        SubscriptionSchedule.Phase current = schedule.getPhases().getFirst();
        String currentPriceId = current.getItems().getFirst().getPrice();

        schedule.update(SubscriptionScheduleUpdateParams.builder()
                .setEndBehavior(SubscriptionScheduleUpdateParams.EndBehavior.RELEASE)
                .addPhase(SubscriptionScheduleUpdateParams.Phase.builder()
                        .addItem(SubscriptionScheduleUpdateParams.Phase.Item.builder()
                                .setPrice(currentPriceId)
                                .build())
                        .setStartDate(current.getStartDate())
                        .setEndDate(current.getEndDate())
                        .build())
                .addPhase(SubscriptionScheduleUpdateParams.Phase.builder()
                        .addItem(SubscriptionScheduleUpdateParams.Phase.Item.builder()
                                .setPrice(this.priceResolver.priceIdFor(target))
                                .build())
                        // One billing cycle on the target plan, then the schedule releases and the
                        // subscription renews normally on it. All plans bill monthly.
                        .setDuration(SubscriptionScheduleUpdateParams.Phase.Duration.builder()
                                .setInterval(SubscriptionScheduleUpdateParams.Phase.Duration.Interval.MONTH)
                                .setIntervalCount(1L)
                                .build())
                        .setProrationBehavior(SubscriptionScheduleUpdateParams.Phase.ProrationBehavior.NONE)
                        .build())
                .build());
    }

    @Override
    public void resume(String stripeSubscriptionId) {
        try {
            com.stripe.model.Subscription subscription =
                    com.stripe.model.Subscription.retrieve(stripeSubscriptionId);
            if (subscription.getSchedule() != null) {
                SubscriptionSchedule.retrieve(subscription.getSchedule()).release();
            }
            if (Boolean.TRUE.equals(subscription.getCancelAtPeriodEnd())) {
                subscription.update(SubscriptionUpdateParams.builder().setCancelAtPeriodEnd(false).build());
            }
        } catch (StripeException e) {
            throw new BillingActivationFailedException(e.getMessage());
        }
    }

    @Override
    public BillingSchedule fetchSchedule(String stripeSubscriptionId) {
        try {
            com.stripe.model.Subscription subscription =
                    com.stripe.model.Subscription.retrieve(stripeSubscriptionId);
            // Since Basil (2025-03-31) the billing period lives on the subscription item, not the
            // subscription root. Our subscriptions carry a single item (one price), so read the first.
            Long periodEndEpoch = subscription.getItems().getData().getFirst().getCurrentPeriodEnd();
            Instant periodEnd = periodEndEpoch == null ? null : Instant.ofEpochSecond(periodEndEpoch);
            return new BillingSchedule(periodEnd, this.resolvePendingPlan(subscription));
        } catch (StripeException e) {
            throw new BillingActivationFailedException(e.getMessage());
        }
    }

    /**
     * The plan a scheduled downgrade will drop to, or {@code null} if none is pending. A pending
     * cancellation ({@code cancel_at_period_end}) means FREE; a subscription schedule means the price
     * of its next (future) phase mapped back to a plan.
     */
    private SubscriptionPlan resolvePendingPlan(com.stripe.model.Subscription subscription) throws StripeException {
        if (Boolean.TRUE.equals(subscription.getCancelAtPeriodEnd())) return SubscriptionPlan.FREE;
        if (subscription.getSchedule() == null) return null;

        SubscriptionSchedule schedule = SubscriptionSchedule.retrieve(subscription.getSchedule());
        long now = Instant.now().getEpochSecond();
        for (SubscriptionSchedule.Phase phase : schedule.getPhases()) {
            if (phase.getStartDate() != null && phase.getStartDate() > now) {
                return this.priceResolver.planForPrice(phase.getItems().getFirst().getPrice());
            }
        }
        return null;
    }

    @Override
    public List<InvoiceView> listInvoices(String stripeCustomerId) {
        try {
            return Invoice.list(InvoiceListParams.builder()
                            .setCustomer(stripeCustomerId)
                            .setLimit(24L)
                            .build())
                    .getData().stream()
                    .map(StripeBillingGateway::toInvoiceView)
                    .toList();
        } catch (StripeException e) {
            throw new InvoiceRetrievalFailedException(e.getMessage());
        }
    }

    private static InvoiceView toInvoiceView(Invoice invoice) {
        Long createdEpoch = invoice.getCreated();
        Instant createdAt = createdEpoch == null ? null : Instant.ofEpochSecond(createdEpoch);
        long amountPaid = invoice.getAmountPaid() == null ? 0L : invoice.getAmountPaid();
        return new InvoiceView(
                invoice.getNumber(),
                invoice.getStatus(),
                amountPaid,
                invoice.getCurrency(),
                createdAt,
                invoice.getHostedInvoiceUrl(),
                invoice.getInvoicePdf());
    }
}
