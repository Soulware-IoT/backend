package site.soulware.cocina360.security.domain.model.command;

import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;

import java.util.UUID;

/**
 * Recalibrate a claimed device's safety thresholds. The new limits fully replace the
 * device's current ones; the change is audited to the requester.
 */
public record UpdateIoTDeviceThresholdsCommand(
    UUID deviceId,
    SafetyThresholds thresholds,
    UUID requesterId
) {}
