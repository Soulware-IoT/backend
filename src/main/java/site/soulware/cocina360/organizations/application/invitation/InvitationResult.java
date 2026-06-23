package site.soulware.cocina360.organizations.application.invitation;

import site.soulware.cocina360.organizations.domain.model.aggregate.Invitation;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationStatus;
import site.soulware.cocina360.profiles.interfaces.acl.ProfileSummary;

import java.time.Instant;
import java.util.UUID;

public record InvitationResult(
        UUID id,
        String invitedEmail,
        UUID organizationId,
        ProfileSummary invitedBy,
        Instant invitedAt,
        Instant respondedAt,
        InvitationStatus status
) {
    public static InvitationResult from(Invitation invitation, ProfileSummary invitedBy) {
        return new InvitationResult(
                invitation.getId().value(),
                invitation.getInvitedEmail(),
                invitation.getOrganizationId().value(),
                invitedBy,
                invitation.getInvitedAt(),
                invitation.getRespondedAt(),
                invitation.getStatus()
        );
    }
}
