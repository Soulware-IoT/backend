package site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import site.soulware.cocina360.internalcontrol.domain.model.command.ReplaceFormatFieldsCommand;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FieldType;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FormatFieldDraft;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FormatFieldId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ValidationRules;

import java.util.List;
import java.util.UUID;

/**
 * Whole-collection replace ({@code PUT}) of a format's fields. Each item is mapped to a
 * {@link FormatFieldDraft} by presence of {@code id}: with id → {@code Existing} (an update),
 * without id → {@code New} (the server assigns the id). The {@code key} is server-owned (derived
 * from the label and frozen at creation), so it is never part of the request.
 */
public record ReplaceFormatFieldsRequest(@NotNull @Valid List<Item> fields) {

    public ReplaceFormatFieldsCommand toCommand(UUID formatId) {
        return new ReplaceFormatFieldsCommand(formatId, this.fields.stream().map(Item::toDraft).toList());
    }

    public record Item(
        UUID id,
        @NotBlank String label,
        @NotNull FieldType type,
        boolean required,
        int displayOrder,
        @Valid ValidationRulesPayload validationRules
    ) {

        FormatFieldDraft toDraft() {
            ValidationRules rules = this.validationRules == null
                    ? new ValidationRules.None()
                    : this.validationRules.toDomain();
            return this.id == null
                    ? new FormatFieldDraft.New(this.label, this.type, this.required, this.displayOrder, rules)
                    : new FormatFieldDraft.Existing(FormatFieldId.of(this.id), this.label, this.type, this.required, this.displayOrder, rules);
        }
    }
}
