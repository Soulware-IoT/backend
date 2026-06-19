package site.soulware.cocina360.security.interfaces.rest.device.response;

import site.soulware.cocina360.security.application.device.DeviceResult;
import site.soulware.cocina360.security.domain.model.valueobject.ActivationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * @param apiKey the provisioned device→edge credential. Returned so the owner can flash
 *               the physical device; the edge replicates it to authenticate the device.
 */
public record DeviceResponse(
    UUID deviceId,
    UUID organizationId,
    String code,
    String name,
    ActivationStatus status,
    String apiKey,
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
                result.apiKey(),
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
