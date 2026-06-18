package site.soulware.cocina360.organizations.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.organizations.domain.model.command.CreateOrganizationCommand;

import java.util.UUID;

public record CreateOrganizationRequest(
        @NotBlank String name,
        String imageUrl,
        String addressLineOne,
        String addressLineTwo,
        String addressReference,
        Double latitude,
        Double longitude
) {
    public CreateOrganizationCommand toCommand(UUID requesterId) {
        return new CreateOrganizationCommand(UUID.randomUUID(), this.name, this.imageUrl,
                this.addressLineOne, this.addressLineTwo, this.addressReference,
                this.latitude, this.longitude, requesterId);
    }
}
