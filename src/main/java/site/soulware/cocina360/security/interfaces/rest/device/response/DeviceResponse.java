package site.soulware.cocina360.security.interfaces.rest.device.response;

import site.soulware.cocina360.security.application.device.DeviceResult;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Management view of a device. The apiKey is intentionally absent — it is only ever
 * returned at the factory provisioning step. Organization, name, and audit fields are
 * null while the device is still {@code PROVISIONED} (unclaimed).
 */
public record DeviceResponse(
    UUID deviceId,
    UUID organizationId,
    String code,
    String name,
    DeviceStatus status,
    Thresholds thresholds,
    Instant createdAt,
    UUID createdBy,
    Instant updatedAt,
    UUID updatedBy
) {

    public record Thresholds(
        int warnTemperatureC,
        int critTemperatureC,
        double warnGasPpm,
        double critGasPpm
    ) {}

    public static DeviceResponse from(DeviceResult result) {
        return new DeviceResponse(
                result.deviceId(),
                result.organizationId(),
                result.code(),
                result.name(),
                result.status(),
                new Thresholds(
                        result.warnTemperatureC(),
                        result.critTemperatureC(),
                        result.warnGasPpm(),
                        result.critGasPpm()),
                result.createdAt(),
                result.createdBy(),
                result.updatedAt(),
                result.updatedBy()
        );
    }
}
