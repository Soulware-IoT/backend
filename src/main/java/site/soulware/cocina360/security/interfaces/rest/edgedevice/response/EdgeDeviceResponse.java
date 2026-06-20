package site.soulware.cocina360.security.interfaces.rest.edgedevice.response;

import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceResult;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * @param apiKey the provisioned edge→backend credential. Returned so the edge can be
 *               configured; it presents this to identify itself and pull its
 *               organization's device registry and thresholds.
 */
public record EdgeDeviceResponse(
    UUID edgeDeviceId,
    UUID organizationId,
    String code,
    String name,
    EdgeDeviceStatus status,
    Instant createdAt,
    UUID createdBy,
    Instant updatedAt,
    UUID updatedBy
) {

    public static EdgeDeviceResponse from(EdgeDeviceResult result) {
        return new EdgeDeviceResponse(
                result.edgeDeviceId(),
                result.organizationId(),
                result.code(),
                result.name(),
                result.status(),
                result.createdAt(),
                result.createdBy(),
                result.updatedAt(),
                result.updatedBy()
        );
    }
}
