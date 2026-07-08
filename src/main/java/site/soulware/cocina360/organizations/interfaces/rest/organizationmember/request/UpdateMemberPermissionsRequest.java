package site.soulware.cocina360.organizations.interfaces.rest.organizationmember.request;

import jakarta.validation.constraints.NotNull;
import site.soulware.cocina360.organizations.domain.model.command.UpdateMemberPermissionsCommand;
import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;

import java.util.UUID;

public record UpdateMemberPermissionsRequest(
        @NotNull PermissionLevel security,
        @NotNull PermissionLevel organizations,
        @NotNull PermissionLevel internalControl
) {
    public UpdateMemberPermissionsCommand toCommand(UUID organizationId, UUID memberId, UUID requesterId) {
        return new UpdateMemberPermissionsCommand(organizationId, memberId, requesterId,
                this.security, this.organizations, this.internalControl);
    }
}
