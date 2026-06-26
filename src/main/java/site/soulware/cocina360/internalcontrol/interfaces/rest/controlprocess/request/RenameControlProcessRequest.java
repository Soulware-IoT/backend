package site.soulware.cocina360.internalcontrol.interfaces.rest.controlprocess.request;

import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.internalcontrol.domain.model.command.RenameControlProcessCommand;

import java.util.UUID;

public record RenameControlProcessRequest(@NotBlank String name) {

    public RenameControlProcessCommand toCommand(UUID id) {
        return new RenameControlProcessCommand(id, this.name);
    }
}
