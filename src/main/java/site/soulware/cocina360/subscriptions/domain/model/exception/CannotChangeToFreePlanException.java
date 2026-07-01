package site.soulware.cocina360.subscriptions.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

/**
 * Thrown when a plan change targets FREE. Leaving a paid plan is a distinct operation
 * ({@code POST /subscription/downgrade}) that also cancels Stripe billing — the plan-change
 * endpoint only moves between paid plans.
 */
public class CannotChangeToFreePlanException extends BusinessRuleViolationException {

    public CannotChangeToFreePlanException() {
        super("error.subscription.free_requires_downgrade");
    }
}
