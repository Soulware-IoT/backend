package site.soulware.cocina360.organizations.domain.model.command;

import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationAddress;

import java.util.UUID;

public record UpdateOrganizationCommand(
        UUID organizationId,
        String name,
        String imageUrl,
        OrganizationAddress address,
        UUID requesterId
) {}
