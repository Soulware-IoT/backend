package site.soulware.cocina360.organizations.domain.model.valueobject;

import site.soulware.cocina360.organizations.domain.model.exception.AdminPermissionNotGrantableException;
import site.soulware.cocina360.shared.domain.model.valueobject.ValueObject;

public record OrganizationMemberPermissions(
    PermissionLevel security,
    PermissionLevel organizations,
    PermissionLevel internalControl
) implements ValueObject {

    /**
     * Build a permissions set, rejecting any attempt to grant {@code ADMIN} on any
     * resource. Used by the member permissions update flow, where admin is reserved and
     * may not be assigned to a member.
     *
     * @throws AdminPermissionNotGrantableException if any resource is set to {@code ADMIN}.
     */
    public static OrganizationMemberPermissions requireNonAdmin(
        PermissionLevel security,
        PermissionLevel organizations,
        PermissionLevel internalControl
    ) {
        if (security == PermissionLevel.ADMIN
                || organizations == PermissionLevel.ADMIN
                || internalControl == PermissionLevel.ADMIN) {
            throw new AdminPermissionNotGrantableException();
        }
        return new OrganizationMemberPermissions(security, organizations, internalControl);
    }
}
