package site.soulware.cocina360.subscriptions.infrastructure.external.stripe.webhook;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.subscriptions.application.subscription.SubscriptionCommandService;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;
import site.soulware.cocina360.subscriptions.infrastructure.external.stripe.billing.StripePriceResolver;
import site.soulware.cocina360.subscriptions.infrastructure.external.stripe.webhook.jpa.ProcessedStripeEventJpaEntity;
import site.soulware.cocina360.subscriptions.infrastructure.external.stripe.webhook.jpa.ProcessedStripeEventJpaRepository;

import java.time.Instant;

/**
 * Entry point for Stripe webhook events. Verifies the signature, deduplicates by Stripe's
 * event id (webhooks are at-least-once delivery), then dispatches to the domain via
 * {@link SubscriptionCommandService}.
 */
@Service
public class StripeWebhookService {

    @Value("${stripe.webhook-secret}")
    private final String webhookSecret = null;

    private final SubscriptionCommandService commandService;
    private final ProcessedStripeEventJpaRepository processedEvents;
    private final StripePriceResolver priceResolver;

    public StripeWebhookService(
        SubscriptionCommandService commandService,
        ProcessedStripeEventJpaRepository processedEvents,
        StripePriceResolver priceResolver
    ) {
        this.commandService = commandService;
        this.processedEvents = processedEvents;
        this.priceResolver = priceResolver;
    }

    @Transactional
    public void handle(String payload, String signatureHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, this.webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new IllegalArgumentException("Invalid Stripe webhook signature", e);
        }

        if (this.processedEvents.existsById(event.getId())) return;

        this.dispatch(event);
        this.processedEvents.save(new ProcessedStripeEventJpaEntity(event.getId(), Instant.now()));
    }

    private void dispatch(Event event) {
        switch (event.getType()) {
            // Payment failures are left to Stripe's dunning/retry cycle. Only when Stripe gives up and
            // deletes the subscription do we drop the org to FREE.
            case "customer.subscription.deleted" ->
                    this.commandService.downgradeToFreeByStripeCustomer(this.subscriptionOf(event).getCustomer());
            // A scheduled paid→paid downgrade advanced its phase at period end → reconcile the local plan
            // with the price Stripe now bills. Upgrades are applied synchronously, so this is a no-op there.
            case "customer.subscription.updated" -> this.syncPlan(this.subscriptionOf(event));
            default -> { /* other event types are not relevant to this service */ }
        }
    }

    private void syncPlan(com.stripe.model.Subscription subscription) {
        String priceId = subscription.getItems().getData().getFirst().getPrice().getId();
        SubscriptionPlan plan = this.priceResolver.planForPrice(priceId);
        if (plan == null) return; // unrecognized price — nothing to reconcile
        this.commandService.syncPlanByStripeCustomer(subscription.getCustomer(), plan);
    }

    private com.stripe.model.Subscription subscriptionOf(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = deserializer.getObject()
                .orElseThrow(() -> new IllegalArgumentException("Unrecognized Stripe event payload"));

        if (stripeObject instanceof com.stripe.model.Subscription subscription) return subscription;

        throw new IllegalArgumentException("Unsupported Stripe event object: " + stripeObject.getClass());
    }
}
