package site.soulware.cocina360.organizations.domain.model.event;

import site.soulware.cocina360.shared.domain.model.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record OrganizationMemberJoined(UUID memberId, UUID profileId, UUID organizationId, Instant occurredOn) implements DomainEvent {}
