package site.soulware.cocina360.shared.domain.model.exception;

/**
 * Thrown when an explicit business rule is violated inside a domain operation.
 * <p>
 * In its core it behaves like any other {@link DomainException}: subclasses pass their
 * own i18n {@code messageKey} (and optional args), which {@code GlobalExceptionHandler}
 * resolves via {@code MessageSource}. The "business rule violated" framing is added by
 * the handler as a translated prefix ({@code error.business_rule.violated}), not baked
 * into the key here — so each rule still surfaces its own specific, translated message.
 */
public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }
}
