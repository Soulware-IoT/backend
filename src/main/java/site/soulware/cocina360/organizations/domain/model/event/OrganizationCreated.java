package site.soulware.cocina360.organizations.domain.model.event;

import site.soulware.cocina360.shared.domain.model.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record OrganizationCreated(UUID organizationId, String name, UUID createdBy, Instant occurredOn) implements DomainEvent {}
