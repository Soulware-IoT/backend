package site.soulware.cocina360.security.domain.model.command;

import java.util.UUID;

/** Rename a claimed IoT device; audited to the requester. */
public record RenameIoTDeviceCommand(
    UUID deviceId,
    String name,
    UUID requesterId
) {}
