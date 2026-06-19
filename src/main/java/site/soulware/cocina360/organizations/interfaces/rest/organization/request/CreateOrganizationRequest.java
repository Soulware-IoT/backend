package site.soulware.cocina360.organizations.interfaces.rest.organization.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.organizations.domain.model.command.CreateOrganizationCommand;

import java.util.UUID;

public record CreateOrganizationRequest(
        @NotBlank String name,
        @Schema(description = "Optional. Organization logo/image URL.") String imageUrl,
        @Schema(description = "Optional. Primary address line.") String addressLineOne,
        @Schema(description = "Optional. Secondary address line.") String addressLineTwo,
        @Schema(description = "Optional. Additional location reference.") String addressReference
) {
    public CreateOrganizationCommand toCommand(UUID requesterId) {
        return new CreateOrganizationCommand(this.name, this.imageUrl,
                this.addressLineOne, this.addressLineTwo, this.addressReference,
                requesterId);
    }
}
