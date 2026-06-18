package site.soulware.cocina360.organizations.domain.model.event;

import site.soulware.cocina360.shared.domain.model.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record InvitationSent(UUID invitationId, String invitedEmail, UUID organizationId, UUID invitedBy, Instant occurredOn) implements DomainEvent {}
