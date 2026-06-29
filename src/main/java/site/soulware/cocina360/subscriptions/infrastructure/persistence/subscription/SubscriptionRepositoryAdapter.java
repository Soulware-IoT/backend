package site.soulware.cocina360.subscriptions.infrastructure.persistence.subscription;

import org.springframework.stereotype.Repository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;
import site.soulware.cocina360.subscriptions.domain.model.aggregate.Subscription;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionId;
import site.soulware.cocina360.subscriptions.domain.repository.SubscriptionRepository;
import site.soulware.cocina360.subscriptions.infrastructure.persistence.subscription.jpa.SubscriptionJpaEntity;
import site.soulware.cocina360.subscriptions.infrastructure.persistence.subscription.jpa.SubscriptionJpaRepository;

import java.util.Optional;

@Repository
public class SubscriptionRepositoryAdapter implements SubscriptionRepository {

    private final SubscriptionJpaRepository jpaRepository;

    public SubscriptionRepositoryAdapter(SubscriptionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Subscription save(Subscription aggregate) {
        SubscriptionJpaEntity saved = this.jpaRepository.save(this.toJpaEntity(aggregate));
        return this.toDomain(saved);
    }

    @Override
    public Optional<Subscription> findById(SubscriptionId id) {
        return this.jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Subscription> findByOrganizationId(OrganizationId organizationId) {
        return this.jpaRepository.findByOrganizationId(organizationId.value()).map(this::toDomain);
    }

    @Override
    public void delete(Subscription aggregate) {
        this.jpaRepository.deleteById(aggregate.getId().value());
    }

    private SubscriptionJpaEntity toJpaEntity(Subscription subscription) {
        return new SubscriptionJpaEntity(
                subscription.getId().value(),
                subscription.getOrganizationId().value(),
                subscription.getOwnedBy().value(),
                subscription.getPlan(),
                subscription.getStatus(),
                subscription.getCreatedAt(),
                subscription.getUpdatedAt()
        );
    }

    private Subscription toDomain(SubscriptionJpaEntity entity) {
        return Subscription.rehydrate(
                SubscriptionId.of(entity.getId()),
                OrganizationId.of(entity.getOrganizationId()),
                ProfileId.of(entity.getOwnedBy()),
                entity.getPlan(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
