package site.soulware.cocina360.shared.domain.model.exception;

/**
 * Base class for all domain-layer exceptions. Thrown when a business rule or
 * invariant is violated. These are unchecked so they propagate naturally up
 * to the application/presentation layer, where they are translated into
 * appropriate error responses (e.g., HTTP 422 Unprocessable Entity).
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
