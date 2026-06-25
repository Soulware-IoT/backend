package site.soulware.cocina360.internalcontrol.domain.model.command;

import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ValidationRules;

import java.util.UUID;

public record UpdateFormatFieldCommand(
    UUID formatId,
    UUID fieldId,
    String label,
    boolean required,
    int displayOrder,
    ValidationRules validationRules
) {}
