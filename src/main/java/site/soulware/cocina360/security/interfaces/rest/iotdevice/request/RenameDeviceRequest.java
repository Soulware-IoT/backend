package site.soulware.cocina360.security.interfaces.rest.iotdevice.request;

import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.security.domain.model.command.RenameIoTDeviceCommand;

import java.util.UUID;

/** Rename a claimed IoT device. */
public record RenameDeviceRequest(
    @NotBlank String name
) {

    public RenameIoTDeviceCommand toCommand(UUID deviceId, UUID requesterId) {
        return new RenameIoTDeviceCommand(deviceId, this.name, requesterId);
    }
}
