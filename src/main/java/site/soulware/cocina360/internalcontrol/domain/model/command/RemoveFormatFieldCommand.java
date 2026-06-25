package site.soulware.cocina360.internalcontrol.domain.model.command;

import java.util.UUID;

public record RemoveFormatFieldCommand(UUID formatId, UUID fieldId) {}
