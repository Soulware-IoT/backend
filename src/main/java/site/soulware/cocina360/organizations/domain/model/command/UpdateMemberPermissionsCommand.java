package site.soulware.cocina360.organizations.domain.model.command;

import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;

import java.util.UUID;

public record UpdateMemberPermissionsCommand(
        UUID organizationId,
        UUID memberId,
        PermissionLevel security,
        PermissionLevel organizations,
        PermissionLevel internalControl
) {}
