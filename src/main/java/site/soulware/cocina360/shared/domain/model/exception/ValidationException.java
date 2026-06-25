package site.soulware.cocina360.shared.domain.model.exception;

import java.util.List;

/**
 * A business-rule violation that aggregates <b>multiple</b> field-level failures rather than a
 * single message — the input was structurally accepted but several values broke their constraints.
 * Maps to HTTP 422 like any {@link BusinessRuleViolationException}, but additionally exposes the
 * per-field {@link FieldViolation} list so the REST handler can surface exactly which field failed
 * and why. The exception's own {@code messageKey} is the summary (e.g. "validation failed"); each
 * violation carries its own i18n key for the specific reason.
 */
public class ValidationException extends BusinessRuleViolationException {

    /** One rejected field: the offending key and the i18n key describing why it failed. */
    public record FieldViolation(String fieldKey, String messageKey) {}

    private final transient List<FieldViolation> violations;

    public ValidationException(String messageKey, List<FieldViolation> violations) {
        super(messageKey);
        this.violations = List.copyOf(violations);
    }

    public List<FieldViolation> getViolations() {
        return this.violations;
    }
}
