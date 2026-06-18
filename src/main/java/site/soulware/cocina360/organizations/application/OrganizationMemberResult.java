package site.soulware.cocina360.organizations.application;

import site.soulware.cocina360.organizations.domain.model.aggregate.OrganizationMember;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberPermissions;

import java.time.Instant;
import java.util.UUID;

public record OrganizationMemberResult(
        UUID id,
        UUID profileId,
        UUID organizationId,
        UUID invitationId,
        Instant joinedAt,
        OrganizationMemberPermissions permissions
) {
    public static OrganizationMemberResult from(OrganizationMember member) {
        return new OrganizationMemberResult(
                member.getId().value(),
                member.getProfileId().value(),
                member.getOrganizationId().value(),
                member.getInvitationId() != null ? member.getInvitationId().value() : null,
                member.getJoinedAt(),
                member.getPermissions()
        );
    }
}
