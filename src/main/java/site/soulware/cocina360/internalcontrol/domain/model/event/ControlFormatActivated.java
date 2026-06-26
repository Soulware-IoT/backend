package site.soulware.cocina360.internalcontrol.domain.model.event;

import site.soulware.cocina360.shared.domain.model.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ControlFormatActivated(UUID formatId, Instant occurredOn) implements DomainEvent {}
