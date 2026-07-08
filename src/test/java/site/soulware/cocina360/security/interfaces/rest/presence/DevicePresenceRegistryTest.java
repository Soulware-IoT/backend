package site.soulware.cocina360.security.interfaces.rest.presence;

import org.junit.jupiter.api.Test;

import site.soulware.cocina360.security.interfaces.rest.presence.response.DevicePresenceResponse;
import site.soulware.cocina360.security.interfaces.rest.sse.OrganizationSseHub;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DevicePresenceRegistryTest {

    private final UUID organizationId = UUID.randomUUID();
    private final UUID deviceId = UUID.randomUUID();

    private static final class MutableClock extends Clock {
        private Instant now = Instant.parse("2026-01-01T00:00:00Z");

        @Override
        public ZoneId getZone() { return ZoneOffset.UTC; }

        @Override
        public Clock withZone(ZoneId zone) { return this; }

        @Override
        public Instant instant() { return this.now; }

        void advance(Duration duration) { this.now = this.now.plus(duration); }
    }

    @Test
    void firstTouchBroadcastsOnline() {
        OrganizationSseHub hub = mock(OrganizationSseHub.class);
        DevicePresenceRegistry registry = new DevicePresenceRegistry(hub);

        registry.touch(this.organizationId, this.deviceId, "DEV-1", DeviceKind.IOT);

        verify(hub).broadcast(
                eq(this.organizationId), eq("presence"), eq("presence"),
                argThat(this::isOnline));
    }

    @Test
    void repeatedTouchWhileOnlineDoesNotRebroadcast() {
        OrganizationSseHub hub = mock(OrganizationSseHub.class);
        DevicePresenceRegistry registry = new DevicePresenceRegistry(hub);

        registry.touch(this.organizationId, this.deviceId, "DEV-1", DeviceKind.IOT);
        registry.touch(this.organizationId, this.deviceId, "DEV-1", DeviceKind.IOT);

        verify(hub, times(1)).broadcast(any(), any(), any(), any());
    }

    @Test
    void sweepFlipsStaleDeviceToOfflineExactlyOnce() {
        OrganizationSseHub hub = mock(OrganizationSseHub.class);
        DevicePresenceRegistry registry = new DevicePresenceRegistry(hub);
        MutableClock clock = new MutableClock();
        registry.useClock(clock);

        registry.touch(this.organizationId, this.deviceId, "DEV-1", DeviceKind.IOT);
        clock.advance(Duration.ofSeconds(31));
        registry.sweep();
        registry.sweep();

        List<DevicePresenceResponse> snapshot = registry.snapshot(this.organizationId);
        assertThat(snapshot).hasSize(1);
        assertThat(snapshot.get(0).status()).isEqualTo(PresenceStatus.OFFLINE);

        verify(hub, times(1)).broadcast(
                eq(this.organizationId), eq("presence"), eq("presence"), argThat(this::isOffline));
    }

    @Test
    void touchAfterGoingOfflineBroadcastsOnlineAgain() {
        OrganizationSseHub hub = mock(OrganizationSseHub.class);
        DevicePresenceRegistry registry = new DevicePresenceRegistry(hub);
        MutableClock clock = new MutableClock();
        registry.useClock(clock);

        registry.touch(this.organizationId, this.deviceId, "DEV-1", DeviceKind.IOT);
        clock.advance(Duration.ofSeconds(31));
        registry.sweep();
        registry.touch(this.organizationId, this.deviceId, "DEV-1", DeviceKind.IOT);

        List<DevicePresenceResponse> snapshot = registry.snapshot(this.organizationId);
        assertThat(snapshot.get(0).status()).isEqualTo(PresenceStatus.ONLINE);
        verify(hub, times(3)).broadcast(any(), any(), any(), any());
    }

    @Test
    void snapshotIsScopedToTheOrganization() {
        OrganizationSseHub hub = mock(OrganizationSseHub.class);
        DevicePresenceRegistry registry = new DevicePresenceRegistry(hub);
        UUID otherOrg = UUID.randomUUID();

        registry.touch(this.organizationId, this.deviceId, "DEV-1", DeviceKind.IOT);
        registry.touch(otherOrg, UUID.randomUUID(), "DEV-2", DeviceKind.EDGE);

        assertThat(registry.snapshot(this.organizationId)).hasSize(1);
        assertThat(registry.snapshot(otherOrg)).hasSize(1);
    }

    private boolean isOnline(Object payload) {
        return ((DevicePresenceResponse) payload).status() == PresenceStatus.ONLINE;
    }

    private boolean isOffline(Object payload) {
        return ((DevicePresenceResponse) payload).status() == PresenceStatus.OFFLINE;
    }
}
