package site.soulware.cocina360.security.domain.model.event;

import site.soulware.cocina360.shared.domain.model.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record EdgeDeviceDeactivated(UUID edgeDeviceId, Instant occurredOn) implements DomainEvent {}
