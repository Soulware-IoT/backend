package site.soulware.cocina360.security.interfaces.rest.edgegateway.request;

import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.security.domain.model.command.RegisterEdgeGatewayCommand;

import java.util.UUID;

public record RegisterEdgeGatewayRequest(
    @NotBlank String name
) {

    public RegisterEdgeGatewayCommand toCommand(UUID organizationId, UUID requesterId) {
        return new RegisterEdgeGatewayCommand(organizationId, this.name, requesterId);
    }
}
