package site.soulware.cocina360.organizations.interfaces.acl;

import java.util.UUID;

/**
 * Anti-corruption layer port of the {@code organizations} context for <b>authorization</b>
 * (Spring Modulith named interface {@code "acl"}).
 * <p>
 * Lets any bounded context enforce the permission matrix without importing organizations
 * internals: it reads the requester's {@code OrganizationMember} permissions <b>live from the
 * database on every call</b> (anti-staleness — the JWT's role claim is never trusted) and
 * compares them against the required minimum. Signatures use only the published authorization
 * language ({@link PermissionArea}, {@link AccessLevel}) and primitives.
 */
public interface AuthorizationApi {

    /**
     * Guards an action: verifies the profile is a member of the organization and that its level
     * in {@code area} meets {@code minimum}.
     *
     * @throws site.soulware.cocina360.organizations.domain.model.exception.InsufficientPermissionException
     *         if the member's level is below {@code minimum} or the profile is not a member
     */
    void requirePermission(UUID organizationId, UUID profileId, PermissionArea area, AccessLevel minimum);

    /**
     * Returns the requester's current level in {@code area}, or {@link AccessLevel#NONE} if the
     * profile is not a member. A projection (never throws) for callers that need the level itself
     * rather than a pass/fail guard.
     */
    AccessLevel currentLevel(UUID organizationId, UUID profileId, PermissionArea area);
}
