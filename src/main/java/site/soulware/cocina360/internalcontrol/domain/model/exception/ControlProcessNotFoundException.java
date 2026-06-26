package site.soulware.cocina360.internalcontrol.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class ControlProcessNotFoundException extends EntityNotFoundException {

    private ControlProcessNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static ControlProcessNotFoundException byId(UUID id) {
        return new ControlProcessNotFoundException("error.control.process.not_found_by_id", id);
    }
}
