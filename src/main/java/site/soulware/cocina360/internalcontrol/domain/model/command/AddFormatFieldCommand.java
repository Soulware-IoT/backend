package site.soulware.cocina360.internalcontrol.domain.model.command;

import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FieldType;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ValidationRules;

import java.util.UUID;

public record AddFormatFieldCommand(
    UUID formatId,
    String key,
    String label,
    FieldType type,
    boolean required,
    int displayOrder,
    ValidationRules validationRules
) {}
