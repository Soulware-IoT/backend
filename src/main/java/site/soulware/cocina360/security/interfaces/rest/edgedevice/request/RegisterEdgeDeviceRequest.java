package site.soulware.cocina360.security.interfaces.rest.edgedevice.request;

import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.security.domain.model.command.RegisterEdgeDeviceCommand;

import java.util.UUID;

public record RegisterEdgeDeviceRequest(
    @NotBlank String name
) {

    public RegisterEdgeDeviceCommand toCommand(UUID organizationId, UUID requesterId) {
        return new RegisterEdgeDeviceCommand(organizationId, this.name, requesterId);
    }
}
