package site.soulware.cocina360.internalcontrol.domain.model.exception;

import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatStatus;

public class CannotResumeFormatException extends InvalidFormatTransitionException {

    public CannotResumeFormatException(ControlFormatStatus currentStatus) {
        super("error.control.format.cannot_resume", currentStatus.label());
    }
}
