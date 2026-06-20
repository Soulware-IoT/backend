package site.soulware.cocina360.organizations.domain.model.command;

import java.util.UUID;

public record DeclineInvitationCommand(UUID invitationId, UUID requesterId) {}
