package site.soulware.cocina360.subscriptions.domain.model.command;

import java.util.UUID;

public record ResumeSubscriptionCommand(UUID organizationId, UUID requesterId) {}
