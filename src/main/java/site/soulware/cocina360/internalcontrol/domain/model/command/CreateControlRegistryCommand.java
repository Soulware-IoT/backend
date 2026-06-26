package site.soulware.cocina360.internalcontrol.domain.model.command;

import java.util.Map;
import java.util.UUID;

public record CreateControlRegistryCommand(UUID formatId, Map<String, Object> data) {}
