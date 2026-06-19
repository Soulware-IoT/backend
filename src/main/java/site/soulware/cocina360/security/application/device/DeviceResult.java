package site.soulware.cocina360.security.application.device;

import site.soulware.cocina360.security.domain.model.aggregate.Device;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceStatus;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.time.Instant;
import java.util.UUID;

/**
 * Management read model for a device. Deliberately excludes the apiKey — the secret
 * is only exposed once, at the factory provisioning step. Fields that are only set
 * once a device is claimed (organization, name, audit) are null while provisioned.
 */
public record DeviceResult(
    UUID deviceId,
    UUID organizationId,
    String code,
    String name,
    DeviceStatus status,
    int warnTemperatureC,
    int critTemperatureC,
    double warnGasPpm,
    double critGasPpm,
    Instant createdAt,
    UUID createdBy,
    Instant updatedAt,
    UUID updatedBy
) {

    public static DeviceResult from(Device device) {
        return new DeviceResult(
                device.getId().value(),
                value(device.getOrganizationId()),
                device.getCode().value(),
                device.getName(),
                device.getStatus(),
                device.getThresholds().warnTemperatureC(),
                device.getThresholds().critTemperatureC(),
                device.getThresholds().warnGasPpm(),
                device.getThresholds().critGasPpm(),
                device.getCreatedAt(),
                value(device.getCreatedBy()),
                device.getUpdatedAt(),
                value(device.getUpdatedBy())
        );
    }

    private static UUID value(OrganizationId id) {
        return id == null ? null : id.value();
    }

    private static UUID value(ProfileId id) {
        return id == null ? null : id.value();
    }
}
