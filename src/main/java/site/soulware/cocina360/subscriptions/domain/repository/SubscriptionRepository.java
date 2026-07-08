package site.soulware.cocina360.subscriptions.domain.repository;

import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.repository.DomainRepository;
import site.soulware.cocina360.subscriptions.domain.model.aggregate.Subscription;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionId;

import java.util.Optional;

public interface SubscriptionRepository extends DomainRepository<Subscription, SubscriptionId> {

    Optional<Subscription> findByOrganizationId(OrganizationId organizationId);

    Optional<Subscription> findByStripeCustomerId(String stripeCustomerId);
}
