package site.soulware.cocina360.profiles.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

public class NotProfileOwnerException extends BusinessRuleViolationException {

    public NotProfileOwnerException() {
        super("error.profile.not_owner");
    }
}
