package site.soulware.cocina360.organizations.interfaces.rest.response;

import site.soulware.cocina360.organizations.application.InvitationResult;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationStatus;

import java.time.Instant;
import java.util.UUID;

public record InvitationResponse(
        UUID id,
        String invitedEmail,
        UUID organizationId,
        UUID invitedBy,
        Instant invitedAt,
        Instant respondedAt,
        InvitationStatus status
) {
    public static InvitationResponse from(InvitationResult result) {
        return new InvitationResponse(result.id(), result.invitedEmail(), result.organizationId(),
                result.invitedBy(), result.invitedAt(), result.respondedAt(), result.status());
    }
}
