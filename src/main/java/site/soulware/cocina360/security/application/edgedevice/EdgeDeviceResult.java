package site.soulware.cocina360.security.application.edgedevice;

import site.soulware.cocina360.security.domain.model.aggregate.EdgeDevice;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceStatus;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.time.Instant;
import java.util.UUID;

/**
 * Management read model for an edge device. Deliberately excludes the apiKey — the secret
 * is only exposed once, at the factory provisioning step. Fields that are only set once
 * an edge device is claimed (organization, name, audit) are null while provisioned.
 */
public record EdgeDeviceResult(
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

    public static EdgeDeviceResult from(EdgeDevice edgeDevice) {
        return new EdgeDeviceResult(
                edgeDevice.getId().value(),
                value(edgeDevice.getOrganizationId()),
                edgeDevice.getCode().value(),
                edgeDevice.getName(),
                edgeDevice.getStatus(),
                edgeDevice.getCreatedAt(),
                value(edgeDevice.getCreatedBy()),
                edgeDevice.getUpdatedAt(),
                value(edgeDevice.getUpdatedBy())
        );
    }

    private static UUID value(OrganizationId id) {
        return id == null ? null : id.value();
    }

    private static UUID value(ProfileId id) {
        return id == null ? null : id.value();
    }
}
