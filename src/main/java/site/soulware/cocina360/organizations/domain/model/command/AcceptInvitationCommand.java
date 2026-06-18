package site.soulware.cocina360.organizations.domain.model.command;

import java.util.UUID;

public record AcceptInvitationCommand(UUID invitationId, UUID acceptingProfileId) {}
