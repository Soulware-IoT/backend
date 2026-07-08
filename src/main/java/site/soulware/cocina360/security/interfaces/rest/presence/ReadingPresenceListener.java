package site.soulware.cocina360.security.interfaces.rest.presence;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import site.soulware.cocina360.security.domain.model.event.ReadingRecorded;

import java.time.Duration;
import java.time.Instant;

/**
 * Derives IoT device liveness from the telemetry flow: the device reports a reading
 * every ~5s and the edge forwards all of them, so each {@link ReadingRecorded} is an
 * implicit per-device heartbeat. This is the only signal that reflects the physical
 * device — the edge's registry poll only proves the <i>edge</i> is up (it lists devices
 * by claim status, not reachability), so it must not drive device presence.
 *
 * <p>Readings older than {@link #FRESHNESS} are ignored: the edge's outbox retries
 * after backend outages, and a stale backlog flush must not flap a meanwhile-dead
 * device ONLINE.
 */
@Component
class ReadingPresenceListener {

    private static final Duration FRESHNESS = Duration.ofSeconds(30);

    private final DevicePresenceRegistry registry;

    ReadingPresenceListener(DevicePresenceRegistry registry) {
        this.registry = registry;
    }

    @ApplicationModuleListener
    void on(ReadingRecorded event) {
        if (Duration.between(event.occurredAt(), Instant.now()).compareTo(FRESHNESS) > 0) {
            return;
        }
        this.registry.touch(
                event.organizationId(), event.deviceId(), event.deviceCode(), DeviceKind.IOT);
    }
}
