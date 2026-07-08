package site.soulware.cocina360.subscriptions.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

public class BillingActivationFailedException extends BusinessRuleViolationException {

    public BillingActivationFailedException(String reason) {
        super("error.subscription.billing_activation_failed", reason);
    }
}
