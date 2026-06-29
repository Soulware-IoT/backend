package site.soulware.cocina360.subscriptions.domain.model.event;

import site.soulware.cocina360.shared.domain.model.event.DomainEvent;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionStatus;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionStatusChanged(
        UUID subscriptionId,
        UUID organizationId,
        SubscriptionStatus previousStatus,
        SubscriptionStatus newStatus,
        Instant occurredOn
) implements DomainEvent {}
