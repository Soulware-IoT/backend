package site.soulware.cocina360.security.interfaces.rest.edgedevice.request;

import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.security.domain.model.command.ClaimEdgeDeviceCommand;

import java.util.UUID;

/**
 * Claim a factory-provisioned edge device into the organization. The owner supplies the
 * edge device's {@code code} (read off the edge) — the apiKey is already held by the
 * backend from provisioning and is never entered here.
 */
public record ClaimEdgeDeviceRequest(
    @NotBlank String code,
    @NotBlank String name
) {

    public ClaimEdgeDeviceCommand toCommand(UUID organizationId, UUID requesterId) {
        return new ClaimEdgeDeviceCommand(organizationId, this.code, this.name, requesterId);
    }
}
