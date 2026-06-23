package site.soulware.cocina360.security.interfaces.rest.edgedevice.response;

import java.util.UUID;

import site.soulware.cocina360.security.application.edgedevice.ProvisionedEdgeDeviceResult;

/**
 * Returned only by the factory provisioning endpoint. Carries the edge device's code and
 * apiKey so they can be written into the edge's configuration — the single point at which
 * the apiKey is exposed.
 */
public record ProvisionedEdgeDeviceResponse(UUID id, String code, String apiKey) {

    public static ProvisionedEdgeDeviceResponse from(ProvisionedEdgeDeviceResult result) {
        return new ProvisionedEdgeDeviceResponse(result.edgeDeviceId(), result.code(), result.apiKey());
    }
}
