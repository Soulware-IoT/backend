package site.soulware.cocina360.security.domain.model.command;

import java.util.UUID;

/** Put a claimed IoT device back in service; audited to the requester. */
public record ActivateIoTDeviceCommand(
    UUID deviceId,
    UUID requesterId
) {}
