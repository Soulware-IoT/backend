package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

import java.util.UUID;

/**
 * Enforces the 1:1 invariant between an organization and its edge gateway: an
 * organization may register at most one edge.
 */
public class OrganizationAlreadyHasEdgeGatewayException extends BusinessRuleViolationException {

    public OrganizationAlreadyHasEdgeGatewayException(UUID organizationId) {
        super("error.edge_gateway.already_exists_for_organization");
    }
}
