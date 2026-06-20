package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

/**
 * Thrown when a management operation (rename, activate, deactivate) is attempted on an
 * edge device that is still {@code PROVISIONED} — i.e. it has not yet been claimed by an
 * organization.
 */
public class EdgeDeviceNotClaimedException extends BusinessRuleViolationException {

    public EdgeDeviceNotClaimedException(String edgeDeviceCode) {
        super("error.edge_device.not_claimed");
    }
}
