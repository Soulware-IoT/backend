package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

/**
 * Thrown when claiming an edge device that is not in the {@code PROVISIONED} state —
 * i.e. it has already been claimed by an organization.
 */
public class EdgeDeviceAlreadyClaimedException extends BusinessRuleViolationException {

    public EdgeDeviceAlreadyClaimedException(String edgeDeviceCode) {
        super("error.edge_device.already_claimed");
    }
}
