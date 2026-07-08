package site.soulware.cocina360.security.interfaces.rest.presence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import site.soulware.cocina360.security.interfaces.rest.presence.response.DevicePresenceResponse;
import site.soulware.cocina360.security.interfaces.rest.sse.OrganizationSseHub;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * In-memory, ephemeral tracker of device reachability. Nothing sends an explicit
 * heartbeat — liveness is derived from traffic that already flows: each authenticated
 * edge request marks the <i>edge</i> seen ({@code EdgeController}), and each forwarded
 * reading marks its <i>IoT device</i> seen ({@code ReadingPresenceListener}), both via
 * {@link #touch}. A device is considered {@code ONLINE} until it goes quiet for longer
 * than {@link #TTL}, at which point the scheduled {@link #sweep()} flips it to
 * {@code OFFLINE}.
 *
 * <p>Only transitions are broadcast (repeated touches of an already-{@code ONLINE}
 * device are not newsworthy), via {@link OrganizationSseHub} on the {@link #TOPIC}
 * topic.
 *
 * <p>This is deliberately not persisted domain state — it is recomputed entirely from
 * live traffic and reset on restart, so it lives in the interfaces layer rather than
 * the domain/repository layers.
 */
@Component
public class DevicePresenceRegistry {

    private static final Logger log = LoggerFactory.getLogger(DevicePresenceRegistry.class);

    static final String TOPIC = "presence";
    private static final String PRESENCE_EVENT = "presence";
    private static final Duration TTL = Duration.ofSeconds(30);

    private record Key(UUID organizationId, UUID deviceId) {}

    private record Entry(String deviceCode, DeviceKind kind, PresenceStatus status, Instant lastSeenAt) {}

    private final OrganizationSseHub hub;
    private final Map<Key, Entry> entries = new HashMap<>();
    private Clock clock = Clock.systemUTC();

    public DevicePresenceRegistry(OrganizationSseHub hub) {
        this.hub = hub;
    }

    /**
     * Record that a device was just heard from. Flips it to {@code ONLINE} and
     * broadcasts if it was previously unknown or {@code OFFLINE}; otherwise just
     * refreshes its last-seen timestamp.
     */
    public synchronized void touch(UUID organizationId, UUID deviceId, String deviceCode, DeviceKind kind) {
        Key key = new Key(organizationId, deviceId);
        Entry previous = this.entries.get(key);
        boolean wasOffline = previous == null || previous.status() == PresenceStatus.OFFLINE;

        Entry entry = new Entry(deviceCode, kind, PresenceStatus.ONLINE, Instant.now(this.clock));
        this.entries.put(key, entry);

        if (wasOffline) {
            log.info("Device {} ({}) of org {} is now ONLINE", deviceId, kind, organizationId);
            this.hub.broadcast(organizationId, TOPIC, PRESENCE_EVENT, this.toResponse(deviceId, entry));
        }
    }

    /**
     * Flip any {@code ONLINE} device that has gone quiet for longer than {@link #TTL}
     * to {@code OFFLINE}, broadcasting exactly once per transition.
     */
    @Scheduled(fixedRate = 5_000)
    synchronized void sweep() {
        Instant now = Instant.now(this.clock);
        this.entries.replaceAll((key, entry) -> {
            if (entry.status() == PresenceStatus.ONLINE
                    && Duration.between(entry.lastSeenAt(), now).compareTo(TTL) > 0) {
                Entry offline = new Entry(entry.deviceCode(), entry.kind(), PresenceStatus.OFFLINE, entry.lastSeenAt());
                log.info("Device {} ({}) of org {} is now OFFLINE", key.deviceId(), entry.kind(), key.organizationId());
                this.hub.broadcast(
                        key.organizationId(), TOPIC, PRESENCE_EVENT, this.toResponse(key.deviceId(), offline));
                return offline;
            }
            return entry;
        });
    }

    /** Current status of every device of the organization this registry has ever seen. */
    public synchronized List<DevicePresenceResponse> snapshot(UUID organizationId) {
        return this.entries.entrySet().stream()
                .filter(e -> e.getKey().organizationId().equals(organizationId))
                .map(e -> this.toResponse(e.getKey().deviceId(), e.getValue()))
                .toList();
    }

    private DevicePresenceResponse toResponse(UUID deviceId, Entry entry) {
        return DevicePresenceResponse.from(
                deviceId, entry.deviceCode(), entry.kind(), entry.status(), entry.lastSeenAt());
    }

    void useClock(Clock clock) {
        this.clock = clock;
    }
}
