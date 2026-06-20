package site.soulware.cocina360.security.domain.model.command;

import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;

import java.util.UUID;

/**
 * Partial update of a claimed IoT device. Each field is optional: a {@code null} field is
 * left unchanged. {@code activate} maps the activation transition — {@code true} activates,
 * {@code false} deactivates, {@code null} leaves the status untouched.
 */
public record UpdateIoTDeviceCommand(
    UUID deviceId,
    String name,
    SafetyThresholds thresholds,
    Boolean activate,
    UUID requesterId
) {}
