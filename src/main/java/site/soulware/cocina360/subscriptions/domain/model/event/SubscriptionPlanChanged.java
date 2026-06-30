package site.soulware.cocina360.subscriptions.domain.model.event;

import site.soulware.cocina360.shared.domain.model.event.DomainEvent;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionPlanChanged(
        UUID subscriptionId,
        UUID organizationId,
        SubscriptionPlan previousPlan,
        SubscriptionPlan newPlan,
        Instant occurredOn
) implements DomainEvent {}
