package site.soulware.cocina360.security.interfaces.rest.reading;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReadingSseRegistryTest {

    private final UUID orgA = UUID.randomUUID();
    private final UUID orgB = UUID.randomUUID();

    /**
     * Registry with the raw write seam overridden: records every attempted delivery
     * and throws {@link IOException} for emitters marked as dead, exercising the
     * guarded send path without a servlet connection.
     */
    private static class RecordingRegistry extends ReadingSseRegistry {

        final List<SseEmitter> writes = new ArrayList<>();
        final Set<SseEmitter> dead = new HashSet<>();

        @Override
        void write(SseEmitter emitter, SseEmitter.SseEventBuilder event) throws IOException {
            if (this.dead.contains(emitter)) {
                throw new IOException("client went away");
            }
            this.writes.add(emitter);
        }
    }

    @Test
    void broadcastReachesOnlyTheOrganizationsSubscribers() {
        RecordingRegistry registry = new RecordingRegistry();
        SseEmitter firstOfA = registry.subscribe(this.orgA);
        SseEmitter secondOfA = registry.subscribe(this.orgA);
        SseEmitter ofB = registry.subscribe(this.orgB);

        registry.broadcast(this.orgA, "reading", "payload");

        assertThat(registry.writes).containsExactly(firstOfA, secondOfA);
        assertThat(registry.writes).doesNotContain(ofB);
    }

    @Test
    void broadcastToOrganizationWithoutSubscribersIsANoOp() {
        RecordingRegistry registry = new RecordingRegistry();

        registry.broadcast(this.orgA, "reading", "payload");

        assertThat(registry.writes).isEmpty();
    }

    @Test
    void deadSubscriberIsRemovedWithoutBlockingDeliveryToTheRest() {
        RecordingRegistry registry = new RecordingRegistry();
        SseEmitter deadEmitter = registry.subscribe(this.orgA);
        SseEmitter liveEmitter = registry.subscribe(this.orgA);
        registry.dead.add(deadEmitter);

        registry.broadcast(this.orgA, "reading", "payload");

        assertThat(registry.writes).containsExactly(liveEmitter);
        assertThat(registry.activeCount(this.orgA)).isEqualTo(1);

        registry.broadcast(this.orgA, "reading", "payload");

        assertThat(registry.writes).containsExactly(liveEmitter, liveEmitter);
    }

    @Test
    void heartbeatReachesEveryOrganizationAndDetectsDeadClients() {
        RecordingRegistry registry = new RecordingRegistry();
        SseEmitter ofA = registry.subscribe(this.orgA);
        SseEmitter deadOfB = registry.subscribe(this.orgB);
        registry.dead.add(deadOfB);

        registry.heartbeat();

        assertThat(registry.writes).containsExactly(ofA);
        assertThat(registry.activeCount(this.orgA)).isEqualTo(1);
        assertThat(registry.activeCount(this.orgB)).isZero();
    }
}
