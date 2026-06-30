package site.soulware.cocina360.organizations.interfaces.rest.organization.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.organizations.domain.model.command.CreateOrganizationCommand;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationAddress;

import java.util.UUID;

public record CreateOrganizationRequest(
        @NotBlank String name,
        @Schema(description = "Optional. Organization logo/image URL.") String imageUrl,
        @Schema(description = "Optional. Organization address.") Address address
) {

    public record Address(String lineOne, String lineTwo, String reference) {}

    public CreateOrganizationCommand toCommand(UUID requesterId) {
        OrganizationAddress organizationAddress = this.address != null
                ? new OrganizationAddress(this.address.lineOne(), this.address.lineTwo(), this.address.reference())
                : new OrganizationAddress(null, null, null);
        return new CreateOrganizationCommand(this.name, this.imageUrl, organizationAddress, requesterId);
    }
}
