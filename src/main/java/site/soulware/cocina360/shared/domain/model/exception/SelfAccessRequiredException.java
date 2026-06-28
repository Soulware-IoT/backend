package site.soulware.cocina360.shared.domain.model.exception;

/**
 * Thrown when a requester targets a self-scoped resource (e.g. "my organizations", "my
 * invitations") that belongs to a different profile than the authenticated one. Maps to HTTP 403.
 */
public class SelfAccessRequiredException extends ForbiddenException {

    public SelfAccessRequiredException() {
        super("error.authz.self_access_required");
    }
}
