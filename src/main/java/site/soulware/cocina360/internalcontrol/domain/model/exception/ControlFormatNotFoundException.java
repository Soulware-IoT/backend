package site.soulware.cocina360.internalcontrol.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class ControlFormatNotFoundException extends EntityNotFoundException {

    private ControlFormatNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static ControlFormatNotFoundException byId(UUID id) {
        return new ControlFormatNotFoundException("error.control.format.not_found_by_id", id);
    }
}
