package site.soulware.cocina360.organizations.domain.model.valueobject;

import site.soulware.cocina360.shared.domain.model.valueobject.ValueObject;

public record OrganizationMemberPermissions(
    PermissionLevel security,
    PermissionLevel iot,
    PermissionLevel internalControl
) implements ValueObject {}
