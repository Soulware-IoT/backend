package site.soulware.cocina360.internalcontrol.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.DomainException;

import java.util.List;

public class RegistryValidationException extends DomainException {

    public record FieldViolation(String fieldKey, String messageKey) {}

    private final List<FieldViolation> violations;

    public RegistryValidationException(List<FieldViolation> violations) {
        super("error.control.registry.validation_failed");
        this.violations = List.copyOf(violations);
    }

    public List<FieldViolation> getViolations() {
        return this.violations;
    }
}
