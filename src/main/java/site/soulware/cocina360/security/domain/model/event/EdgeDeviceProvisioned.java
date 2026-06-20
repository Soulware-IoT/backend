package site.soulware.cocina360.security.domain.model.event;

import site.soulware.cocina360.shared.domain.model.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Raised when an edge device is minted at the factory step (code + apiKey generated)
 * but not yet claimed by any organization.
 */
public record EdgeDeviceProvisioned(
    UUID edgeDeviceId,
    String edgeDeviceCode,
    Instant occurredOn
) implements DomainEvent {}
