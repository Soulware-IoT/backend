package site.soulware.cocina360.security.application.edgegateway;

import site.soulware.cocina360.security.domain.model.aggregate.EdgeGateway;
import site.soulware.cocina360.security.domain.model.valueobject.ActivationStatus;

import java.time.Instant;
import java.util.UUID;

public record EdgeGatewayResult(
    UUID edgeGatewayId,
    UUID organizationId,
    String name,
    ActivationStatus status,
    String apiKey,
    Instant createdAt,
    Instant updatedAt
) {

    public static EdgeGatewayResult from(EdgeGateway gateway) {
        return new EdgeGatewayResult(
                gateway.getId().value(),
                gateway.getOrganizationId().value(),
                gateway.getName(),
                gateway.getStatus(),
                gateway.getApiKey().value(),
                gateway.getCreatedAt(),
                gateway.getUpdatedAt()
        );
    }
}
