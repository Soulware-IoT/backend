package site.soulware.cocina360.organizations.domain.model.command;

import java.util.UUID;

public record RemoveOrganizationMemberCommand(UUID organizationId, UUID memberId) {}
