package site.soulware.cocina360.security.domain.model.event;

import site.soulware.cocina360.security.domain.model.valueobject.SafetySeverity;
import site.soulware.cocina360.shared.domain.model.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Raised for every reading persisted to the ledger, regardless of severity. The hook
 * for live telemetry consumers (e.g. the SSE stream fanning readings out to connected
 * frontends). Carries the owning organization so listeners can route by org without a
 * device lookup.
 *
 * @param occurredAt when the reading happened at the edge/iot-device
 * @param occurredOn when this backend persisted it (the ledger's {@code recordedAt})
 */
public record ReadingRecorded(
    UUID organizationId,
    UUID readingId,
    UUID deviceId,
    int temperatureC,
    double gasPpm,
    SafetySeverity severity,
    Instant occurredAt,
    Instant occurredOn
) implements DomainEvent {}
