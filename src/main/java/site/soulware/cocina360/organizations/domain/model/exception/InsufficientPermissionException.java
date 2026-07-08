package site.soulware.cocina360.organizations.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.ForbiddenException;

/**
 * Thrown when a requester's permission level in an organization area is below the minimum the
 * action requires (or the requester is not a member of the organization). Maps to HTTP 403.
 */
public class InsufficientPermissionException extends ForbiddenException {

    public InsufficientPermissionException() {
        super("error.authz.insufficient_permission");
    }
}
