package site.soulware.cocina360.security.domain.model.command;

import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;

import java.util.UUID;

/**
 * Claim a factory-provisioned device (identified by its {@code code}) into an
 * organization.
 *
 * @param thresholds optional calibration limits; when {@code null} the device's
 *                   hardcoded defaults are applied.
 */
public record ClaimDeviceCommand(
    UUID organizationId,
    String code,
    String name,
    SafetyThresholds thresholds,
    UUID requesterId
) {}
