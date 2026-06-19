package site.soulware.cocina360.organizations.application.organizationmember;

import site.soulware.cocina360.organizations.domain.model.aggregate.OrganizationMember;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberPermissions;
import site.soulware.cocina360.profiles.interfaces.acl.ProfileSummary;

import java.time.Instant;
import java.util.UUID;

public record OrganizationMemberResult(
        UUID id,
        UUID organizationId,
        UUID invitationId,
        Instant joinedAt,
        OrganizationMemberPermissions permissions,
        ProfileSummary profile
) {
    public static OrganizationMemberResult from(OrganizationMember member, ProfileSummary profile) {
        return new OrganizationMemberResult(
                member.getId().value(),
                member.getOrganizationId().value(),
                member.getInvitationId() != null ? member.getInvitationId().value() : null,
                member.getJoinedAt(),
                member.getPermissions(),
                profile
        );
    }
}
