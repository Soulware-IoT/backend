package site.soulware.cocina360.internalcontrol.interfaces.rest.controlprocess.request;

import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.internalcontrol.domain.model.command.CreateControlProcessCommand;

import java.util.UUID;

public record CreateControlProcessRequest(@NotBlank String name) {

    public CreateControlProcessCommand toCommand(UUID organizationId) {
        return new CreateControlProcessCommand(organizationId, this.name);
    }
}
