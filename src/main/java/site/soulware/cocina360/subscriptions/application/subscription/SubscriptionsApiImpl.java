package site.soulware.cocina360.subscriptions.application.subscription;

import org.springframework.stereotype.Service;
import site.soulware.cocina360.subscriptions.domain.model.exception.DeviceQuotaExceededException;
import site.soulware.cocina360.subscriptions.domain.model.query.GetSubscriptionByOrganizationQuery;
import site.soulware.cocina360.subscriptions.interfaces.acl.SubscriptionsApi;

import java.util.UUID;

@Service
class SubscriptionsApiImpl implements SubscriptionsApi {

    private final SubscriptionQueryService queryService;

    SubscriptionsApiImpl(SubscriptionQueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    public int deviceQuotaFor(UUID organizationId) {
        return this.queryService.handle(new GetSubscriptionByOrganizationQuery(organizationId))
                .plan()
                .maxIotDevices();
    }

    @Override
    public void enforceDeviceQuota(UUID organizationId, long currentDeviceCount) {
        int quota = this.deviceQuotaFor(organizationId);
        if (quota != -1 && currentDeviceCount >= quota) {
            throw new DeviceQuotaExceededException(currentDeviceCount, quota);
        }
    }
}
