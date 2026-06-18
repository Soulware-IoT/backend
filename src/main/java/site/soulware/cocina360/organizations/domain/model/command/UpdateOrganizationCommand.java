package site.soulware.cocina360.organizations.domain.model.command;

import java.util.UUID;

public record UpdateOrganizationCommand(
        UUID organizationId,
        String name,
        String imageUrl,
        String addressLineOne,
        String addressLineTwo,
        String addressReference,
        Double latitude,
        Double longitude,
        UUID requesterId
) {}
