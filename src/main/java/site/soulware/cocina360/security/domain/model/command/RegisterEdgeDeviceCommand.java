package site.soulware.cocina360.security.domain.model.command;

import java.util.UUID;

public record RegisterEdgeDeviceCommand(UUID organizationId, String name, UUID requesterId) {}
