package site.soulware.cocina360.organizations.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.ForbiddenException;

/**
 * Raised when a member tries to grant a permission level that is not strictly below their own
 * level in the organizations area (e.g. a {@code LIEUTENANT} granting {@code LIEUTENANT} or
 * {@code ADMIN}). An actor may only assign levels below their own. Maps to HTTP 403.
 */
public class PermissionGrantTooHighException extends ForbiddenException {

    public PermissionGrantTooHighException() {
        super("error.organization_member.grant_exceeds_own");
    }
}
