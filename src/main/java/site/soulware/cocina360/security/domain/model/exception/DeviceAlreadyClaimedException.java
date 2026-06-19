package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

/**
 * Thrown when claiming a device that is not in the {@code PROVISIONED} state —
 * i.e. it has already been claimed by an organization.
 */
public class DeviceAlreadyClaimedException extends BusinessRuleViolationException {

    public DeviceAlreadyClaimedException(String deviceCode) {
        super("error.device.already_claimed");
    }
}
