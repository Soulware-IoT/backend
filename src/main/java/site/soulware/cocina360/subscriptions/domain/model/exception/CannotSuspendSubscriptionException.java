package site.soulware.cocina360.subscriptions.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionStatus;

public class CannotSuspendSubscriptionException extends BusinessRuleViolationException {

    public CannotSuspendSubscriptionException(SubscriptionStatus currentStatus) {
        super("error.subscription.cannot_suspend", currentStatus);
    }
}
