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

    public StripeWebhookService(
        SubscriptionCommandService commandService,
        ProcessedStripeEventJpaRepository processedEvents
    ) {
        this.commandService = commandService;
        this.processedEvents = processedEvents;
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
        // Payment failures are left to Stripe's dunning/retry cycle. Only when Stripe gives up and
        // deletes the subscription do we drop the org to FREE. A voluntary downgrade is handled
        // synchronously by the command service, not here.
        if ("customer.subscription.deleted".equals(event.getType())) {
            this.commandService.downgradeToFreeByStripeCustomer(this.customerId(event));
        }
    }

    private String customerId(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = deserializer.getObject()
                .orElseThrow(() -> new IllegalArgumentException("Unrecognized Stripe event payload"));

        if (stripeObject instanceof com.stripe.model.Subscription subscription) return subscription.getCustomer();

        throw new IllegalArgumentException("Unsupported Stripe event object: " + stripeObject.getClass());
    }
}
