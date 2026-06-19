package site.soulware.cocina360.security.application.device;

import site.soulware.cocina360.security.domain.model.aggregate.Device;
import site.soulware.cocina360.security.domain.model.valueobject.ActivationStatus;

import java.time.Instant;
import java.util.UUID;

public record DeviceResult(
    UUID deviceId,
    UUID organizationId,
    String code,
    String name,
    ActivationStatus status,
    String apiKey,
    int warnTemperatureC,
    int critTemperatureC,
    double warnGasPpm,
    double critGasPpm,
    Instant createdAt,
    Instant updatedAt
) {

    public static DeviceResult from(Device device) {
        return new DeviceResult(
                device.getId().value(),
                device.getOrganizationId().value(),
                device.getCode().value(),
                device.getName(),
                device.getStatus(),
                device.getApiKey().value(),
                device.getThresholds().warnTemperatureC(),
                device.getThresholds().critTemperatureC(),
                device.getThresholds().warnGasPpm(),
                device.getThresholds().critGasPpm(),
                device.getCreatedAt(),
                device.getUpdatedAt()
        );
    }
}
