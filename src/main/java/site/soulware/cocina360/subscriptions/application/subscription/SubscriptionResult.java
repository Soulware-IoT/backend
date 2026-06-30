package site.soulware.cocina360.subscriptions.application.subscription;

import site.soulware.cocina360.subscriptions.domain.model.aggregate.Subscription;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionStatus;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionResult(
        UUID id,
        UUID organizationId,
        UUID ownedBy,
        SubscriptionPlan plan,
        SubscriptionStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static SubscriptionResult from(Subscription subscription) {
        return new SubscriptionResult(
                subscription.getId().value(),
                subscription.getOrganizationId().value(),
                subscription.getOwnedBy().value(),
                subscription.getPlan(),
                subscription.getStatus(),
                subscription.getCreatedAt(),
                subscription.getUpdatedAt()
        );
    }
}
