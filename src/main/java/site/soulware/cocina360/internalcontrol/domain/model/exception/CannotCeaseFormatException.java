package site.soulware.cocina360.internalcontrol.domain.model.exception;

import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatStatus;

public class CannotCeaseFormatException extends InvalidFormatTransitionException {

    public CannotCeaseFormatException(ControlFormatStatus currentStatus) {
        super("error.control.format.cannot_cease", currentStatus);
    }
}
