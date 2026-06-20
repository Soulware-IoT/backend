package site.soulware.cocina360.security.interfaces.rest.iotdevice.response;

import java.util.UUID;

import site.soulware.cocina360.security.application.iotdevice.ProvisionedIoTDeviceResult;

/**
 * Returned only by the factory provisioning endpoint. Carries the device's code and
 * apiKey so they can be burned into the firmware — the single point at which the
 * apiKey is exposed.
 */
public record ProvisionedDeviceResponse(UUID deviceId, String code, String apiKey) {

    public static ProvisionedDeviceResponse from(ProvisionedIoTDeviceResult result) {
        return new ProvisionedDeviceResponse(result.deviceId(), result.code(), result.apiKey());
    }
}
