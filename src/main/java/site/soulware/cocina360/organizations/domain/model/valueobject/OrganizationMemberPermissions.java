package site.soulware.cocina360.organizations.domain.model.valueobject;

import site.soulware.cocina360.organizations.domain.model.exception.AdminPermissionNotGrantableException;
import site.soulware.cocina360.organizations.domain.model.exception.PermissionGrantTooHighException;
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

    /**
     * Build a permissions set granted by an actor, enforcing that every assigned level is
     * <b>strictly below</b> the actor's own level. This single rule subsumes the admin guard
     * (nobody outranks {@code ADMIN}, so {@code ADMIN} can never be granted): a {@code LIEUTENANT}
     * may assign {@code ASSIGNEE}/{@code NONE}; an {@code ADMIN} may also assign {@code LIEUTENANT}.
     *
     * @throws PermissionGrantTooHighException if any assigned level is not below {@code actorLevel}.
     */
    public static OrganizationMemberPermissions assignableBy(
        PermissionLevel actorLevel,
        PermissionLevel security,
        PermissionLevel organizations,
        PermissionLevel internalControl
    ) {
        requireBelow(security, actorLevel);
        requireBelow(organizations, actorLevel);
        requireBelow(internalControl, actorLevel);
        return new OrganizationMemberPermissions(security, organizations, internalControl);
    }

    private static void requireBelow(PermissionLevel assigned, PermissionLevel actorLevel) {
        if (!assigned.isBelow(actorLevel)) {
            throw new PermissionGrantTooHighException();
        }
    }
}
