package site.soulware.cocina360.subscriptions.interfaces.rest.subscription.request;

import jakarta.validation.constraints.NotNull;
import site.soulware.cocina360.subscriptions.domain.model.command.ChangeSubscriptionPlanCommand;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

import java.util.UUID;

public record ChangeSubscriptionPlanRequest(
        @NotNull SubscriptionPlan plan
) {
    public ChangeSubscriptionPlanCommand toCommand(UUID organizationId, UUID requesterId) {
        return new ChangeSubscriptionPlanCommand(organizationId, requesterId, this.plan);
    }
}
