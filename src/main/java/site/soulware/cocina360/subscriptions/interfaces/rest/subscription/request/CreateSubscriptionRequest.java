package site.soulware.cocina360.subscriptions.interfaces.rest.subscription.request;

import jakarta.validation.constraints.NotNull;
import site.soulware.cocina360.subscriptions.domain.model.command.CreateSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

import java.util.UUID;

public record CreateSubscriptionRequest(
        @NotNull SubscriptionPlan plan
) {
    public CreateSubscriptionCommand toCommand(UUID organizationId, UUID requesterId) {
        return new CreateSubscriptionCommand(organizationId, requesterId, this.plan);
    }
}
