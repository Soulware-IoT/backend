package site.soulware.cocina360.internalcontrol.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.ValidationException;

import java.util.List;

/**
 * Raised when a control registry's submitted values violate their format's field validations.
 * Aggregates one {@link ValidationException.FieldViolation} per failing field so the boundary can
 * report every problem at once (HTTP 422).
 */
public class RegistryValidationException extends ValidationException {

    public RegistryValidationException(List<FieldViolation> violations) {
        super("error.control.registry.validation_failed", violations);
    }
}
