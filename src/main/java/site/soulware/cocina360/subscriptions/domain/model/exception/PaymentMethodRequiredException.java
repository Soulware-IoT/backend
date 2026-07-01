package site.soulware.cocina360.subscriptions.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

public class PaymentMethodRequiredException extends BusinessRuleViolationException {

    public PaymentMethodRequiredException() {
        super("error.subscription.payment_method_required");
    }
}
