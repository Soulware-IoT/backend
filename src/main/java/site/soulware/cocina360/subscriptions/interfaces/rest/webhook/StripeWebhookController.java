package site.soulware.cocina360.subscriptions.interfaces.rest.webhook;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import site.soulware.cocina360.subscriptions.infrastructure.external.stripe.webhook.StripeWebhookService;

/** No JWT — Stripe authenticates itself via the {@code Stripe-Signature} header. */
@RestController
public class StripeWebhookController {

    private final StripeWebhookService webhookService;

    public StripeWebhookController(StripeWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/stripe/webhooks")
    public ResponseEntity<Void> handle(
        @RequestBody String payload,
        @RequestHeader("Stripe-Signature") String signature
    ) {
        this.webhookService.handle(payload, signature);
        return ResponseEntity.ok().build();
    }
}
