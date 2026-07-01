package site.soulware.cocina360.subscriptions.infrastructure.external.stripe.webhook.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "processed_stripe_events")
public
class ProcessedStripeEventJpaEntity {

    @Id
    private String stripeEventId;

    @SuppressWarnings("unused")
    private Instant processedAt;

    protected ProcessedStripeEventJpaEntity() {}

    public ProcessedStripeEventJpaEntity(String stripeEventId, Instant processedAt) {
        this.stripeEventId = stripeEventId;
        this.processedAt = processedAt;
    }
}
