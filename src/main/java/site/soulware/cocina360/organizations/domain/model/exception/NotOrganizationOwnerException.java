package site.soulware.cocina360.organizations.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

public class NotOrganizationOwnerException extends BusinessRuleViolationException {

    public NotOrganizationOwnerException() {
        super("error.organization.not_owner");
    }
}
