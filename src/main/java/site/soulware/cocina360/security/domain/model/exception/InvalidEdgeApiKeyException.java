package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.UnauthorizedException;

/**
 * Thrown when an edge-facing request carries a missing or unrecognised
 * {@code X-Edge-Api-Key}. Maps to HTTP 401 — the calling edge cannot be
 * authenticated, so it cannot be linked to an organization.
 */
public class InvalidEdgeApiKeyException extends UnauthorizedException {

    public InvalidEdgeApiKeyException() {
        super("error.edge_device.invalid_api_key");
    }
}
