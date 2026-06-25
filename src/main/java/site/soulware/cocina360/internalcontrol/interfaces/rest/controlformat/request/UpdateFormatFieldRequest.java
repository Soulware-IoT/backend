package site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.internalcontrol.domain.model.command.UpdateFormatFieldCommand;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ValidationRules;

import java.util.UUID;

public record UpdateFormatFieldRequest(
    @NotBlank String label,
    boolean required,
    int displayOrder,
    @Valid ValidationRulesPayload validationRules
) {

    public UpdateFormatFieldCommand toCommand(UUID formatId, UUID fieldId) {
        return new UpdateFormatFieldCommand(
                formatId,
                fieldId,
                this.label,
                this.required,
                this.displayOrder,
                this.validationRules == null ? new ValidationRules.None() : this.validationRules.toDomain()
        );
    }
}
