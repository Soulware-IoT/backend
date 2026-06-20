package site.soulware.cocina360.security.interfaces.rest.edgedevice.request;

import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.security.domain.model.command.RenameEdgeDeviceCommand;

import java.util.UUID;

/** Rename a claimed edge device. */
public record RenameEdgeDeviceRequest(
    @NotBlank String name
) {

    public RenameEdgeDeviceCommand toCommand(UUID edgeDeviceId, UUID requesterId) {
        return new RenameEdgeDeviceCommand(edgeDeviceId, this.name, requesterId);
    }
}
