package site.soulware.cocina360.security.domain.model.event;

import site.soulware.cocina360.shared.domain.model.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Raised when a device's safety thresholds change. Of interest to the edge sync,
 * which propagates the new calibration so the device recalibrates on its next poll.
 */
public record DeviceThresholdsUpdated(UUID deviceId, Instant occurredOn) implements DomainEvent {}
