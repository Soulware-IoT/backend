package site.soulware.cocina360.subscriptions.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

public class SubscriptionPlanUnchangedException extends BusinessRuleViolationException {

    public SubscriptionPlanUnchangedException(SubscriptionPlan plan) {
        super("error.subscription.plan_unchanged", plan);
    }
}
