package site.soulware.cocina360.shared.domain.model.exception;

/**
 * Thrown when an explicit business rule is violated inside a domain operation.
 * Use this when a rule check fails that is expressed as a named policy
 * (e.g., "cannot place order when cart is empty").
 */
public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(String rule) {
        super("Business rule violated: " + rule);
    }
}
