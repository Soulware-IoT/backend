package site.soulware.cocina360.subscriptions.interfaces.acl;

import java.util.UUID;

/**
 * Anti-corruption layer port of the {@code subscriptions} context (Spring Modulith named
 * interface {@code "acl"}).
 * <p>
 * Lets other bounded contexts resolve an organization's IoT device quota without importing
 * any of the {@code subscriptions} module's internals. Existence is enforced by reusing the
 * canonical {@code SubscriptionNotFoundException} thrown by the subscriptions query service.
 */
public interface SubscriptionsApi {

    /**
     * Returns the maximum number of IoT devices the organization's active plan allows, or
     * {@code -1} if unlimited.
     *
     * @throws site.soulware.cocina360.subscriptions.domain.model.exception.SubscriptionNotFoundException if absent
     */
    int deviceQuotaFor(UUID organizationId);

    /**
     * Verifies the organization's active plan still has room for one more IoT device, given
     * how many it currently has claimed.
     *
     * @throws site.soulware.cocina360.subscriptions.domain.model.exception.SubscriptionNotFoundException if absent
     * @throws site.soulware.cocina360.subscriptions.domain.model.exception.DeviceQuotaExceededException if the quota is already met
     */
    void enforceDeviceQuota(UUID organizationId, long currentDeviceCount);
}
