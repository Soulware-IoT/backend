package site.soulware.cocina360.internalcontrol.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class FormatFieldNotFoundException extends EntityNotFoundException {

    private FormatFieldNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static FormatFieldNotFoundException byId(UUID id) {
        return new FormatFieldNotFoundException("error.control.format.field.not_found_by_id", id);
    }
}
