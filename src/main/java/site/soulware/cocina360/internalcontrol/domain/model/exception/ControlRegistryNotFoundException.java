package site.soulware.cocina360.internalcontrol.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class ControlRegistryNotFoundException extends EntityNotFoundException {

    private ControlRegistryNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static ControlRegistryNotFoundException byId(UUID id) {
        return new ControlRegistryNotFoundException("error.control.registry.not_found_by_id", id);
    }
}
