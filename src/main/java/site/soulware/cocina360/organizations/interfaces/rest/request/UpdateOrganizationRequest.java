package site.soulware.cocina360.organizations.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.organizations.domain.model.command.UpdateOrganizationCommand;

import java.util.UUID;

public record UpdateOrganizationRequest(
        @NotBlank String name,
        String imageUrl,
        String addressLineOne,
        String addressLineTwo,
        String addressReference
) {
    public UpdateOrganizationCommand toCommand(UUID organizationId, UUID requesterId) {
        return new UpdateOrganizationCommand(organizationId, this.name, this.imageUrl,
                this.addressLineOne, this.addressLineTwo, this.addressReference,
                requesterId);
    }
}
