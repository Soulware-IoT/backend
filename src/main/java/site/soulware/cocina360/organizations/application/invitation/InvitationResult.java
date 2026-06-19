package site.soulware.cocina360.organizations.application.invitation;

import site.soulware.cocina360.organizations.domain.model.aggregate.Invitation;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationStatus;

import java.time.Instant;
import java.util.UUID;

public record InvitationResult(
        UUID id,
        String invitedEmail,
        UUID organizationId,
        UUID invitedBy,
        Instant invitedAt,
        Instant respondedAt,
        InvitationStatus status
) {
    public static InvitationResult from(Invitation invitation) {
        return new InvitationResult(
                invitation.getId().value(),
                invitation.getInvitedEmail(),
                invitation.getOrganizationId().value(),
                invitation.getInvitedBy().value(),
                invitation.getInvitedAt(),
                invitation.getRespondedAt(),
                invitation.getStatus()
        );
    }
}
