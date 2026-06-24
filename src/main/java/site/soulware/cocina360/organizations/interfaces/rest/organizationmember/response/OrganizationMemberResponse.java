package site.soulware.cocina360.organizations.interfaces.rest.organizationmember.response;

import site.soulware.cocina360.organizations.application.organizationmember.OrganizationMemberResult;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberPermissions;
import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;
import site.soulware.cocina360.profiles.interfaces.acl.ProfileSummary;

import java.time.Instant;
import java.util.UUID;

public record OrganizationMemberResponse(
        UUID id,
        UUID organizationId,
        UUID invitationId,
        Instant joinedAt,
        Permissions permissions,
        ProfileSummary profile
) {
    public record Permissions(
            PermissionLevel security,
            PermissionLevel organizations,
            PermissionLevel internalControl
    ) {
        public static Permissions from(OrganizationMemberPermissions organizationMemberPermissions) {
            return new Permissions(
                organizationMemberPermissions.security(),
                organizationMemberPermissions.organizations(),
                organizationMemberPermissions.internalControl()
            );
        }
    }

    public static OrganizationMemberResponse from(OrganizationMemberResult result) {
        return new OrganizationMemberResponse(result.id(), result.organizationId(),
                result.invitationId(), result.joinedAt(),
                Permissions.from(result.permissions()),
                result.profile());
    }
}
