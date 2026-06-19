package site.soulware.cocina360.organizations.interfaces.rest.organizationmember.response;

import site.soulware.cocina360.organizations.application.organizationmember.OrganizationMemberResult;
import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;

import java.time.Instant;
import java.util.UUID;

public record OrganizationMemberResponse(
        UUID id,
        UUID profileId,
        UUID organizationId,
        UUID invitationId,
        Instant joinedAt,
        PermissionLevel securityPermission,
        PermissionLevel iotPermission,
        PermissionLevel internalControlPermission
) {
    public static OrganizationMemberResponse from(OrganizationMemberResult result) {
        return new OrganizationMemberResponse(result.id(), result.profileId(), result.organizationId(),
                result.invitationId(), result.joinedAt(),
                result.permissions().security(),
                result.permissions().iot(),
                result.permissions().internalControl());
    }
}
