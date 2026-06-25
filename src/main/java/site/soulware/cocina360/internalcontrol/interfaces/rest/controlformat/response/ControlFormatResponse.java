package site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.response;

import site.soulware.cocina360.internalcontrol.application.controlformat.ControlFormatResult;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatStatus;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FieldType;
import site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.request.ValidationRulesPayload;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ControlFormatResponse(
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
            ValidationRulesPayload validationRules
    ) {

        static Field from(ControlFormatResult.Field field) {
            return new Field(
                    field.id(),
                    field.key(),
                    field.label(),
                    field.type(),
                    field.required(),
                    field.displayOrder(),
                    ValidationRulesPayload.from(field.validationRules())
            );
        }
    }

    public static ControlFormatResponse from(ControlFormatResult result) {
        return new ControlFormatResponse(
                result.id(),
                result.processId(),
                result.name(),
                result.status(),
                result.fields().stream().map(Field::from).toList(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
