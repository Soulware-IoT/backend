package site.soulware.cocina360.security.domain.model.command;

import java.util.UUID;

/** Rename a claimed edge device; audited to the requester. */
public record RenameEdgeDeviceCommand(
    UUID edgeDeviceId,
    String name,
    UUID requesterId
) {}
