package site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.request;

import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.internalcontrol.domain.model.command.CreateControlFormatCommand;

import java.util.UUID;

public record CreateControlFormatRequest(@NotBlank String name) {

    public CreateControlFormatCommand toCommand(UUID processId) {
        return new CreateControlFormatCommand(processId, this.name);
    }
}
