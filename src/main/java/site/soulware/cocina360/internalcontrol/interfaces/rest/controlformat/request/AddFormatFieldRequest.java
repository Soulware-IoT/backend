package site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import site.soulware.cocina360.internalcontrol.domain.model.command.AddFormatFieldCommand;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FieldType;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ValidationRules;

import java.util.UUID;

public record AddFormatFieldRequest(
    @NotBlank String key,
    @NotBlank String label,
    @NotNull FieldType type,
    boolean required,
    int displayOrder,
    @Valid ValidationRulesPayload validationRules
) {

    public AddFormatFieldCommand toCommand(UUID formatId) {
        return new AddFormatFieldCommand(
                formatId,
                this.key,
                this.label,
                this.type,
                this.required,
                this.displayOrder,
                this.validationRules == null ? new ValidationRules.None() : this.validationRules.toDomain()
        );
    }
}
