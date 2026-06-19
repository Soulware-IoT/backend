package site.soulware.cocina360.security.domain.model.command;

import java.util.UUID;

public record RegisterEdgeGatewayCommand(UUID organizationId, String name) {}
