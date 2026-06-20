package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

import java.util.UUID;

/**
 * Enforces the 1:1 invariant between an organization and its edge device: an
 * organization may register at most one edge.
 */
public class OrganizationAlreadyHasEdgeDeviceException extends BusinessRuleViolationException {

    public OrganizationAlreadyHasEdgeDeviceException(UUID organizationId) {
        super("error.edge_device.already_exists_for_organization");
    }
}
