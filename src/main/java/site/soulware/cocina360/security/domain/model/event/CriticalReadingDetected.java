package site.soulware.cocina360.security.domain.model.event;

import site.soulware.cocina360.shared.domain.model.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Raised when a recorded reading reaches {@code CRITICAL} severity. The hook for
 * the alerts/notifications capability — a listener reacts after the fact to warn
 * the owner. Event-only for now; no listener is wired yet.
 */
public record CriticalReadingDetected(
    UUID readingId,
    UUID deviceId,
    int temperatureC,
    double gasPpm,
    Instant occurredOn
) implements DomainEvent {}
