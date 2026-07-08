package site.soulware.cocina360.security.interfaces.rest.presence.response;

import site.soulware.cocina360.security.interfaces.rest.presence.DeviceKind;
import site.soulware.cocina360.security.interfaces.rest.presence.PresenceStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Payload of a {@code presence} SSE event, and of the presence snapshot endpoint. Carries
 * the device's kind (gateway vs. sensor) so clients can render each accordingly.
 */
public record DevicePresenceResponse(
    UUID deviceId,
    String deviceCode,
    DeviceKind kind,
    PresenceStatus status,
    Instant since
) {

    public static DevicePresenceResponse from(
        UUID deviceId,
        String deviceCode,
        DeviceKind kind,
        PresenceStatus status,
        Instant since
    ) {
        return new DevicePresenceResponse(deviceId, deviceCode, kind, status, since);
    }
}
