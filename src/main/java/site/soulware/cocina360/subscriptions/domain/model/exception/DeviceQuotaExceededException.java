package site.soulware.cocina360.subscriptions.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

public class DeviceQuotaExceededException extends BusinessRuleViolationException {

    public DeviceQuotaExceededException(long current, int quota) {
        super("error.subscription.device_quota_exceeded", current, quota);
    }
}
