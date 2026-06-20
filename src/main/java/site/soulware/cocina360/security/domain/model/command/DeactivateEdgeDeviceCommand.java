package site.soulware.cocina360.security.domain.model.command;

import java.util.UUID;

/** Take a claimed edge device out of service; audited to the requester. */
public record DeactivateEdgeDeviceCommand(
    UUID edgeDeviceId,
    UUID requesterId
) {}
