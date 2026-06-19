package site.soulware.cocina360.internalcontrol.domain.model.exception;

import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatStatus;
import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

import java.util.UUID;

public class FormatNotEditableException extends BusinessRuleViolationException {

    public FormatNotEditableException(UUID formatId, ControlFormatStatus currentStatus) {
        super("error.control.format.not_editable");
    }
}
