package site.soulware.cocina360.internalcontrol.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

/**
 * Base type for control-format lifecycle transition violations. The aggregate's transition
 * guards throw this as a generic signal; each lifecycle operation catches it and rethrows the
 * operation-specific subtype ({@link CannotActivateFormatException}, {@link CannotSuspendFormatException},
 * {@link CannotResumeFormatException}, {@link CannotCeaseFormatException}) so the surfaced message
 * focuses on the attempted operation rather than the raw source/target status pair.
 */
public class InvalidFormatTransitionException extends BusinessRuleViolationException {

    /** Generic signal thrown by the aggregate's transition guards; always rethrown as a specific subtype. */
    public InvalidFormatTransitionException() {
        super("error.control.format.invalid_transition");
    }

    protected InvalidFormatTransitionException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }
}
