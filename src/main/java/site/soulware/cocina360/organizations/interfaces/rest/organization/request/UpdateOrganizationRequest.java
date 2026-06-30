package site.soulware.cocina360.organizations.interfaces.rest.organization.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.organizations.domain.model.command.UpdateOrganizationCommand;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationAddress;

import java.util.UUID;

public record UpdateOrganizationRequest(
        @NotBlank String name,
        @Schema(description = "Optional. Organization logo/image URL.") String imageUrl,
        @Schema(description = "Optional. Organization address.") Address address
) {

    public record Address(String lineOne, String lineTwo, String reference) {}

    public UpdateOrganizationCommand toCommand(UUID organizationId, UUID requesterId) {
        OrganizationAddress organizationAddress = this.address != null
                ? new OrganizationAddress(this.address.lineOne(), this.address.lineTwo(), this.address.reference())
                : new OrganizationAddress(null, null, null);
        return new UpdateOrganizationCommand(organizationId, this.name, this.imageUrl, organizationAddress, requesterId);
    }
}
