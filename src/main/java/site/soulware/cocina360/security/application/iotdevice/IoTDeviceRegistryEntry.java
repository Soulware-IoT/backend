package site.soulware.cocina360.security.application.iotdevice;

import site.soulware.cocina360.security.domain.model.aggregate.IoTDevice;

import java.util.UUID;

/**
 * One device as seen by its organization's edge: enough to serve and authenticate the
 * physical device locally. Unlike the management {@link IoTDeviceResult}, this projection
 * <b>includes</b> the device's apiKey — it is the {@code device → edge} credential the
 * edge replicates so it can authenticate inbound device traffic.
 */
public record IoTDeviceRegistryEntry(
    UUID deviceId,
    String code,
    String name,
    String apiKey,
    int warnTemperatureC,
    int critTemperatureC,
    double warnGasPpm,
    double critGasPpm
) {

    public static IoTDeviceRegistryEntry from(IoTDevice device) {
        return new IoTDeviceRegistryEntry(
                device.getId().value(),
                device.getCode().value(),
                device.getName(),
                device.getApiKey().value(),
                device.getThresholds().warnTemperatureC(),
                device.getThresholds().critTemperatureC(),
                device.getThresholds().warnGasPpm(),
                device.getThresholds().critGasPpm()
        );
    }
}
