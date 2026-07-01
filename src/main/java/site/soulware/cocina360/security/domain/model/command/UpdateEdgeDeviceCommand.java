package site.soulware.cocina360.security.domain.model.command;

import java.util.UUID;

/**
 * Partial update of a claimed edge device. Each field is optional: a {@code null} field is
 * left unchanged. {@code activate} maps the activation transition — {@code true} activates,
 * {@code false} deactivates, {@code null} leaves the status untouched.
 */
public record UpdateEdgeDeviceCommand(
    UUID edgeDeviceId,
    String name,
    Boolean activate,
    UUID requesterId
) {}
