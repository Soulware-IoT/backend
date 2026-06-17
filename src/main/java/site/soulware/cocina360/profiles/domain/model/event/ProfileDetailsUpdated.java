package site.soulware.cocina360.profiles.domain.model.event;

import site.soulware.cocina360.shared.domain.model.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ProfileDetailsUpdated(UUID profileId, Instant occurredOn) implements DomainEvent {
}
