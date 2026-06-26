package site.soulware.cocina360.internalcontrol.interfaces.rest.controlregistry.request;

import jakarta.validation.constraints.NotNull;
import site.soulware.cocina360.internalcontrol.domain.model.command.CreateControlRegistryCommand;

import java.util.Map;
import java.util.UUID;

public record CreateControlRegistryRequest(
    @NotNull Map<String, Object> data
) {

    public CreateControlRegistryCommand toCommand(UUID formatId) {
        return new CreateControlRegistryCommand(formatId, this.data);
    }
}
