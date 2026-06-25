package site.soulware.cocina360.internalcontrol.domain.model.valueobject;

/**
 * Desired state of a single field within a whole-collection replace ({@code PUT} semantics). The
 * sealed split makes identity explicit and {@code null}-free: a {@link New} draft has no id (the
 * aggregate assigns one), while an {@link Existing} draft references a field by id. The
 * {@code key} — the stable handle that binds a field to registry data — is never client-supplied:
 * the aggregate derives it from the label as a slug at creation and freezes it, so it appears on
 * neither variant. {@code type} stays editable: while the format is still DRAFT a field's type may
 * be changed (which replaces its validation rules), and data-incompatibility is prevented by
 * forbidding edits once the format is ACTIVE.
 */
public sealed interface FormatFieldDraft permits FormatFieldDraft.New, FormatFieldDraft.Existing {

    record New(
        String label,
        FieldType type,
        boolean required,
        int displayOrder,
        ValidationRules validationRules
    ) implements FormatFieldDraft {}

    record Existing(
        FormatFieldId id,
        String label,
        FieldType type,
        boolean required,
        int displayOrder,
        ValidationRules validationRules
    ) implements FormatFieldDraft {}
}
