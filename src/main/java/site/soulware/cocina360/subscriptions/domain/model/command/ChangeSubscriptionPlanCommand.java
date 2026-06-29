package site.soulware.cocina360.subscriptions.domain.model.command;

import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

import java.util.UUID;

public record ChangeSubscriptionPlanCommand(
        UUID organizationId,
        UUID requesterId,
        SubscriptionPlan plan
) {}
