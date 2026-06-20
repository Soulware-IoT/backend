package site.soulware.cocina360.organizations.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

import java.util.UUID;

public class InvitationAlreadyRespondedException extends BusinessRuleViolationException {

    public InvitationAlreadyRespondedException(UUID invitationId) {
        super("error.invitation.already_responded");
    }
}
