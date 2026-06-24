package site.soulware.cocina360.internalcontrol.domain.model.exception;

import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatStatus;

public class CannotActivateFormatException extends InvalidFormatTransitionException {

    public CannotActivateFormatException(ControlFormatStatus currentStatus) {
        super("error.control.format.cannot_activate", currentStatus);
    }
}
