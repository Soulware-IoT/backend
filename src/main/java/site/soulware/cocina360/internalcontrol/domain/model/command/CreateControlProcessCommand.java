package site.soulware.cocina360.internalcontrol.domain.model.command;

import java.util.UUID;

public record CreateControlProcessCommand(UUID organizationId, String name) {}
