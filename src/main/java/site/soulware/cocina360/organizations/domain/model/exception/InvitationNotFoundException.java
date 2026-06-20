package site.soulware.cocina360.organizations.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class InvitationNotFoundException extends EntityNotFoundException {

    private InvitationNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static InvitationNotFoundException byId(UUID id) {
        return new InvitationNotFoundException("error.invitation.not_found_by_id", id);
    }
}
