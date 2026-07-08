package site.soulware.cocina360.subscriptions.infrastructure.external.stripe.webhook.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedStripeEventJpaRepository extends JpaRepository<ProcessedStripeEventJpaEntity, String> {
}
