package site.soulware.cocina360.internalcontrol.domain.model.exception;

import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatStatus;
import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

public class InvalidFormatTransitionException extends BusinessRuleViolationException {

    public InvalidFormatTransitionException(ControlFormatStatus from, ControlFormatStatus to) {
        super("error.control.format.invalid_transition");
    }
}
