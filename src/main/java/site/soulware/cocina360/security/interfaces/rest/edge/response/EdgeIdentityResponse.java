package site.soulware.cocina360.security.interfaces.rest.edge.response;

import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceResult;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceStatus;

import java.util.UUID;

/**
 * The linkage view returned to an authenticated edge: who it is and which organization
 * it belongs to. The apiKey is intentionally omitted (the caller already holds it).
 */
public record EdgeIdentityResponse(
    UUID id,
    String code,
    UUID organizationId,
    String name,
    EdgeDeviceStatus status
) {

    public static EdgeIdentityResponse from(EdgeDeviceResult result) {
        return new EdgeIdentityResponse(
                result.edgeDeviceId(),
                result.code(),
                result.organizationId(),
                result.name(),
                result.status()
        );
    }
}
