package site.soulware.cocina360.internalcontrol.application.controlformat;

import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlFormat;
import site.soulware.cocina360.internalcontrol.domain.model.entity.FormatField;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatStatus;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FieldType;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ValidationRules;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ControlFormatResult(
        UUID id,
        UUID processId,
        String name,
        ControlFormatStatus status,
        List<Field> fields,
        Instant createdAt,
        Instant updatedAt
) {

    public record Field(
            UUID id,
            String key,
            String label,
            FieldType type,
            boolean required,
            int displayOrder,
            ValidationRules validationRules
    ) {

        static Field from(FormatField field) {
            return new Field(
                    field.getId().value(),
                    field.getKey(),
                    field.getLabel(),
                    field.getType(),
                    field.isRequired(),
                    field.getDisplayOrder(),
                    field.getValidationRules()
            );
        }
    }

    public static ControlFormatResult from(ControlFormat format) {
        return new ControlFormatResult(
                format.getId().value(),
                format.getProcessId().value(),
                format.getName(),
                format.getStatus(),
                format.getFields().stream().map(Field::from).toList(),
                format.getCreatedAt(),
                format.getUpdatedAt()
        );
    }
}
