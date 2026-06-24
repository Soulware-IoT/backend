package site.soulware.cocina360.organizations.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

/**
 * Raised when an attempt is made to grant {@code ADMIN} level on any resource to an
 * organization member. Admin is reserved and cannot be assigned through the member
 * permissions flow.
 */
public class AdminPermissionNotGrantableException extends BusinessRuleViolationException {

    public AdminPermissionNotGrantableException() {
        super("error.organization_member.admin_not_grantable");
    }
}
