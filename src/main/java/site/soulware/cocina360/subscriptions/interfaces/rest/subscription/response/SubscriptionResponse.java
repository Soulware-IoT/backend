package site.soulware.cocina360.subscriptions.interfaces.rest.subscription.response;

import site.soulware.cocina360.subscriptions.application.subscription.SubscriptionResult;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionStatus;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionResponse(
        UUID id,
        UUID organizationId,
        UUID ownedBy,
        SubscriptionPlan plan,
        SubscriptionStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static SubscriptionResponse from(SubscriptionResult result) {
        return new SubscriptionResponse(
                result.id(),
                result.organizationId(),
                result.ownedBy(),
                result.plan(),
                result.status(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
