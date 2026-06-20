package site.soulware.cocina360.security.application.edgedevice;

import site.soulware.cocina360.security.domain.model.aggregate.EdgeDevice;

import java.util.UUID;

/**
 * The output of the factory provisioning step — the only time the edge device's apiKey
 * is exposed, so it can be written into the edge's configuration alongside its code.
 */
public record ProvisionedEdgeDeviceResult(UUID edgeDeviceId, String code, String apiKey) {

    public static ProvisionedEdgeDeviceResult from(EdgeDevice edgeDevice) {
        return new ProvisionedEdgeDeviceResult(
                edgeDevice.getId().value(),
                edgeDevice.getCode().value(),
                edgeDevice.getApiKey().value()
        );
    }
}
