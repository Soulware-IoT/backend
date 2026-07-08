package site.soulware.cocina360.subscriptions.infrastructure.persistence.subscription.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionJpaEntity, UUID> {

    Optional<SubscriptionJpaEntity> findByOrganizationId(UUID organizationId);

    Optional<SubscriptionJpaEntity> findByStripeCustomerId(String stripeCustomerId);
}
