package site.soulware.cocina360.shared.domain.model.valueobject;

/**
 * Implemented by domain enums whose values are shown to end users and therefore must be
 * translated. The enum only declares its i18n {@code messageKey} (a plain string, no Spring
 * dependency) — exactly like a {@code DomainException} declares its key; the actual resolution
 * happens in infrastructure (the REST {@code MessageResolver}), which replaces any
 * {@code TranslatableEnum} message argument with its locale-resolved label.
 */
public interface TranslatableEnum {

    String messageKey();
}
