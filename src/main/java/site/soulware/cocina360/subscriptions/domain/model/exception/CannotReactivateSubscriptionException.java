package site.soulware.cocina360.subscriptions.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionStatus;

public class CannotReactivateSubscriptionException extends BusinessRuleViolationException {

    public CannotReactivateSubscriptionException(SubscriptionStatus currentStatus) {
        super("error.subscription.cannot_reactivate", currentStatus);
    }
}
