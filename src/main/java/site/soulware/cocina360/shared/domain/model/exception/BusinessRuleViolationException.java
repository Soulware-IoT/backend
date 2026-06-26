package site.soulware.cocina360.shared.domain.model.exception;

/**
 * Thrown when an explicit business rule is violated inside a domain operation. Maps to
 * HTTP 422.
 * <p>
 * It behaves like any other {@link DomainException}: subclasses pass their own i18n
 * {@code messageKey} (and optional args), which {@code GlobalExceptionHandler} resolves
 * via {@code MessageSource} and returns verbatim as the error message — each rule surfaces
 * its own specific, translated message with no added prefix.
 */
public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }
}
