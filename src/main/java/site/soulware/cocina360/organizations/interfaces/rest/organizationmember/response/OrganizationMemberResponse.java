package site.soulware.cocina360.organizations.interfaces.rest.organizationmember.response;

import site.soulware.cocina360.organizations.application.organizationmember.OrganizationMemberResult;
import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;
import site.soulware.cocina360.profiles.interfaces.acl.ProfileSummary;

import java.time.Instant;
import java.util.UUID;

public record OrganizationMemberResponse(
        UUID id,
        UUID organizationId,
        UUID invitationId,
        Instant joinedAt,
        PermissionLevel securityPermission,
        PermissionLevel iotPermission,
        PermissionLevel internalControlPermission,
        ProfileSummary profile
) {
    public static OrganizationMemberResponse from(OrganizationMemberResult result) {
        return new OrganizationMemberResponse(result.id(), result.organizationId(),
                result.invitationId(), result.joinedAt(),
                result.permissions().security(),
                result.permissions().iot(),
                result.permissions().internalControl(),
                result.profile());
    }
}
