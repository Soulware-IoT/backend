package site.soulware.cocina360.security.interfaces.rest.device.response;

import site.soulware.cocina360.security.application.device.ProvisionedDeviceResult;

import java.util.UUID;

/**
 * Returned only by the factory provisioning endpoint. Carries the device's code and
 * apiKey so they can be burned into the firmware — the single point at which the
 * apiKey is exposed.
 */
public record ProvisionedDeviceResponse(UUID deviceId, String code, String apiKey) {

    public static ProvisionedDeviceResponse from(ProvisionedDeviceResult result) {
        return new ProvisionedDeviceResponse(result.deviceId(), result.code(), result.apiKey());
    }
}
