package site.soulware.cocina360.security.domain.model.command;

import java.util.UUID;

/** Take a claimed IoT device out of service; audited to the requester. */
public record DeactivateIoTDeviceCommand(
    UUID deviceId,
    UUID requesterId
) {}
