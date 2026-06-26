package site.soulware.cocina360.internalcontrol.domain.model.command;

import java.util.UUID;

public record CreateControlFormatCommand(UUID processId, String name, boolean createSampleFields) {}
