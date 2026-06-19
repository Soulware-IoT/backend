package site.soulware.cocina360.security.application.device;

import site.soulware.cocina360.security.domain.model.aggregate.Device;

import java.util.UUID;

/**
 * The output of the factory provisioning step — the only time the device's apiKey
 * is exposed, so it can be burned into the firmware alongside its code.
 */
public record ProvisionedDeviceResult(UUID deviceId, String code, String apiKey) {

    public static ProvisionedDeviceResult from(Device device) {
        return new ProvisionedDeviceResult(
                device.getId().value(),
                device.getCode().value(),
                device.getApiKey().value()
        );
    }
}
