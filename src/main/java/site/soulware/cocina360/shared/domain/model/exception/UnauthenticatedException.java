package site.soulware.cocina360.shared.domain.model.exception;

/**
 * Thrown when a request reaches the application without a usable authenticated identity
 * (missing/invalid JWT, or a token without a parseable {@code sub}). Maps to HTTP 401.
 */
public class UnauthenticatedException extends UnauthorizedException {

    public UnauthenticatedException() {
        super("error.auth.unauthenticated");
    }
}
