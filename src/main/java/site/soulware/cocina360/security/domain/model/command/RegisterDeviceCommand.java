package site.soulware.cocina360.security.domain.model.command;

import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;

import java.util.UUID;

/**
 * @param thresholds the device's calibration limits; when {@code null} the device's
 *                   hardcoded defaults are applied at registration.
 */
public record RegisterDeviceCommand(
    UUID organizationId,
    String code,
    String name,
    SafetyThresholds thresholds
) {}
