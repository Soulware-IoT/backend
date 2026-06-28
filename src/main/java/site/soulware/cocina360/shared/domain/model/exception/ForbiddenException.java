package site.soulware.cocina360.shared.domain.model.exception;

/**
 * Thrown when an authenticated requester lacks the rights to perform an action (insufficient
 * permission level, or accessing a resource that is not theirs). Maps to HTTP 403. Subclasses
 * pass their own i18n {@code messageKey}.
 */
public class ForbiddenException extends DomainException {

    public ForbiddenException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }
}
