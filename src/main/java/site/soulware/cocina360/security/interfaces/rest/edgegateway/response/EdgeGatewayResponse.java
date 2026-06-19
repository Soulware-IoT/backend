package site.soulware.cocina360.security.interfaces.rest.edgegateway.response;

import site.soulware.cocina360.security.application.edgegateway.EdgeGatewayResult;
import site.soulware.cocina360.security.domain.model.valueobject.ActivationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * @param apiKey the provisioned edge→backend credential. Returned so the edge can be
 *               configured; it presents this to identify itself and pull its
 *               organization's device registry and thresholds.
 */
public record EdgeGatewayResponse(
    UUID edgeGatewayId,
    UUID organizationId,
    String name,
    ActivationStatus status,
    String apiKey,
    Instant createdAt,
    Instant updatedAt
) {

    public static EdgeGatewayResponse from(EdgeGatewayResult result) {
        return new EdgeGatewayResponse(
                result.edgeGatewayId(),
                result.organizationId(),
                result.name(),
                result.status(),
                result.apiKey(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
