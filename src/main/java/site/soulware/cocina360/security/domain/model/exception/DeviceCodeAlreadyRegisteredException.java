package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

/**
 * Enforces that a hardware {@code DeviceCode} is registered at most once across
 * the fleet, so telemetry and config always resolve to a single device.
 */
public class DeviceCodeAlreadyRegisteredException extends BusinessRuleViolationException {

    public DeviceCodeAlreadyRegisteredException(String deviceCode) {
        super("error.device.code_already_registered");
    }
}
