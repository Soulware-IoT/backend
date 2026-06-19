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
    UUID createdBy,
    Instant updatedAt,
    UUID updatedBy
) {

    public static EdgeGatewayResult from(EdgeGateway gateway) {
        return new EdgeGatewayResult(
                gateway.getId().value(),
                gateway.getOrganizationId().value(),
                gateway.getName(),
                gateway.getStatus(),
                gateway.getApiKey().value(),
                gateway.getCreatedAt(),
                gateway.getCreatedBy().value(),
                gateway.getUpdatedAt(),
                gateway.getUpdatedBy().value()
        );
    }
}
