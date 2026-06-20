package site.soulware.cocina360.security.domain.model.event;

import site.soulware.cocina360.shared.domain.model.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record IoTDeviceRegistered(
    UUID deviceId,
    UUID organizationId,
    String deviceCode,
    Instant occurredOn
) implements DomainEvent {}
