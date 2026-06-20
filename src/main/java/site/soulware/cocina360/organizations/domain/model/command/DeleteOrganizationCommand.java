package site.soulware.cocina360.organizations.domain.model.command;

import java.util.UUID;

public record DeleteOrganizationCommand(UUID organizationId, UUID requesterId) {}
