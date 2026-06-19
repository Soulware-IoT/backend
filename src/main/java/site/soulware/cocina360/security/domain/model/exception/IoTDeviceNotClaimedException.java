package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

/**
 * Thrown when an operation that is only valid for an in-service device (e.g. recalibrating
 * its safety thresholds) is attempted on one that is still {@code PROVISIONED} — i.e. it
 * has not yet been claimed by an organization, so it still carries factory configuration.
 */
public class IoTDeviceNotClaimedException extends BusinessRuleViolationException {

    public IoTDeviceNotClaimedException(String deviceCode) {
        super("error.iot_device.not_claimed");
    }
}
