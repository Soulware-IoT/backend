package site.soulware.cocina360.organizations.interfaces.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.organizations.domain.model.command.InviteToOrganizationCommand;

import java.util.UUID;

public record InviteRequest(@NotBlank @Email String invitedEmail) {

    public InviteToOrganizationCommand toCommand(UUID organizationId, UUID invitedBy) {
        return new InviteToOrganizationCommand(organizationId, this.invitedEmail, invitedBy);
    }
}
