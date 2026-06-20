package site.soulware.cocina360.shared.domain.model.exception;

/**
 * Thrown when a request cannot be authenticated (e.g. a missing or invalid machine
 * credential). Maps to HTTP 401. Subclasses pass their own i18n {@code messageKey}.
 */
public class UnauthorizedException extends DomainException {

    public UnauthorizedException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }
}
