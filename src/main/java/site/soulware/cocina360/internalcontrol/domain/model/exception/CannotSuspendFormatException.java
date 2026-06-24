package site.soulware.cocina360.internalcontrol.domain.model.exception;

import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatStatus;

public class CannotSuspendFormatException extends InvalidFormatTransitionException {

    public CannotSuspendFormatException(ControlFormatStatus currentStatus) {
        super("error.control.format.cannot_suspend", currentStatus);
    }
}
