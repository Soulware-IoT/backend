package site.soulware.cocina360.security.domain.model.command;

import java.util.UUID;

/**
 * Claim a factory-provisioned edge device (identified by its {@code code}) into an
 * organization.
 */
public record ClaimEdgeDeviceCommand(
    UUID organizationId,
    String code,
    String name,
    UUID requesterId
) {}
