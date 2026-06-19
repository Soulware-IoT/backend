package site.soulware.cocina360.organizations.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

public class NotInvitedProfileException extends BusinessRuleViolationException {

    public NotInvitedProfileException() {
        super("error.invitation.not_invited_profile");
    }
}
