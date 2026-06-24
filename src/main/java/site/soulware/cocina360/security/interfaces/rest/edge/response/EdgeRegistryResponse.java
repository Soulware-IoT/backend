package site.soulware.cocina360.security.interfaces.rest.edge.response;

import site.soulware.cocina360.security.application.iotdevice.IoTDeviceRegistryEntry;

import java.util.List;
import java.util.UUID;

/**
 * The registry an authenticated edge polls: its organization plus the in-service IoT
 * devices it must serve. Each entry carries the device's apiKey (the {@code device → edge}
 * credential) and safety thresholds so the edge can authenticate and serve devices locally.
 */
public record EdgeRegistryResponse(
    UUID organizationId,
    List<Device> devices
) {

    public record Device(
        UUID id,
        String code,
        String name,
        String apiKey,
        Thresholds thresholds
    ) {}

    public record Thresholds(
        int warnTemperatureC,
        int critTemperatureC,
        double warnGasPpm,
        double critGasPpm
    ) {}

    public static EdgeRegistryResponse of(UUID organizationId, List<IoTDeviceRegistryEntry> entries) {
        List<Device> devices = entries.stream()
                .map(entry -> new Device(
                        entry.deviceId(),
                        entry.code(),
                        entry.name(),
                        entry.apiKey(),
                        new Thresholds(
                                entry.warnTemperatureC(),
                                entry.critTemperatureC(),
                                entry.warnGasPpm(),
                                entry.critGasPpm())))
                .toList();
        return new EdgeRegistryResponse(organizationId, devices);
    }
}
