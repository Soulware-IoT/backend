package site.soulware.cocina360.subscriptions.domain.model.command;

import java.util.UUID;

public record DowngradeSubscriptionCommand(UUID organizationId, UUID requesterId) {}
