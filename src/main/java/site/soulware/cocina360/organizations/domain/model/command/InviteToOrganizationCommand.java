package site.soulware.cocina360.organizations.domain.model.command;

import java.util.UUID;

public record InviteToOrganizationCommand(UUID organizationId, String invitedEmail, UUID invitedBy) {}
