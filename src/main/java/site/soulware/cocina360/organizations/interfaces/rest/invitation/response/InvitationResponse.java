package site.soulware.cocina360.organizations.interfaces.rest.invitation.response;

import site.soulware.cocina360.organizations.application.invitation.InvitationResult;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationStatus;
import site.soulware.cocina360.profiles.interfaces.acl.ProfileSummary;

import java.time.Instant;
import java.util.UUID;

public record InvitationResponse(
        UUID id,
        String invitedEmail,
        UUID organizationId,
        ProfileSummary invitedBy,
        Instant invitedAt,
        Instant respondedAt,
        InvitationStatus status
) {
    public static InvitationResponse from(InvitationResult result) {
        return new InvitationResponse(result.id(), result.invitedEmail(), result.organizationId(),
                result.invitedBy(), result.invitedAt(), result.respondedAt(), result.status());
    }
}
