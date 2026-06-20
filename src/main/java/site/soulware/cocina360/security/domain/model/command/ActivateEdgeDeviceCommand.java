package site.soulware.cocina360.security.domain.model.command;

import java.util.UUID;

/** Put a claimed edge device back in service; audited to the requester. */
public record ActivateEdgeDeviceCommand(
    UUID edgeDeviceId,
    UUID requesterId
) {}
