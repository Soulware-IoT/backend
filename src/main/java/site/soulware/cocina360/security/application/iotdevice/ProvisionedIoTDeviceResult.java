package site.soulware.cocina360.security.application.iotdevice;

import site.soulware.cocina360.security.domain.model.aggregate.IoTDevice;

import java.util.UUID;

/**
 * The output of the factory provisioning step — the only time the device's apiKey
 * is exposed, so it can be burned into the firmware alongside its code.
 */
public record ProvisionedIoTDeviceResult(UUID deviceId, String code, String apiKey) {

    public static ProvisionedIoTDeviceResult from(IoTDevice device) {
        return new ProvisionedIoTDeviceResult(
                device.getId().value(),
                device.getCode().value(),
                device.getApiKey().value()
        );
    }
}
