package site.soulware.cocina360.subscriptions.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

public class NotSubscriptionOwnerException extends BusinessRuleViolationException {

    public NotSubscriptionOwnerException() {
        super("error.subscription.not_owner");
    }
}
