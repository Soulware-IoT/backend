package site.soulware.cocina360.security.interfaces.rest.sse;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationSseHubTest {

    private final UUID orgA = UUID.randomUUID();
    private final UUID orgB = UUID.randomUUID();

    /**
     * Hub with the raw write seam overridden: records every attempted delivery and
     * throws {@link IOException} for emitters marked as dead, exercising the guarded
     * send path without a servlet connection.
     */
    private static class RecordingHub extends OrganizationSseHub {

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
    void broadcastReachesOnlyTheOrganizationsSubscribersOnTheSameTopic() {
        RecordingHub hub = new RecordingHub();
        SseEmitter firstOfA = hub.subscribe(this.orgA, "readings");
        SseEmitter secondOfA = hub.subscribe(this.orgA, "readings");
        SseEmitter ofB = hub.subscribe(this.orgB, "readings");
        SseEmitter otherTopicOfA = hub.subscribe(this.orgA, "presence");

        hub.broadcast(this.orgA, "readings", "reading", "payload");

        assertThat(hub.writes).containsExactly(firstOfA, secondOfA);
        assertThat(hub.writes).doesNotContain(ofB, otherTopicOfA);
    }

    @Test
    void broadcastToOrganizationWithoutSubscribersIsANoOp() {
        RecordingHub hub = new RecordingHub();

        hub.broadcast(this.orgA, "readings", "reading", "payload");

        assertThat(hub.writes).isEmpty();
    }

    @Test
    void deadSubscriberIsRemovedWithoutBlockingDeliveryToTheRest() {
        RecordingHub hub = new RecordingHub();
        SseEmitter deadEmitter = hub.subscribe(this.orgA, "readings");
        SseEmitter liveEmitter = hub.subscribe(this.orgA, "readings");
        hub.dead.add(deadEmitter);

        hub.broadcast(this.orgA, "readings", "reading", "payload");

        assertThat(hub.writes).containsExactly(liveEmitter);
        assertThat(hub.activeCount(this.orgA, "readings")).isEqualTo(1);

        hub.broadcast(this.orgA, "readings", "reading", "payload");

        assertThat(hub.writes).containsExactly(liveEmitter, liveEmitter);
    }

    @Test
    void heartbeatReachesEveryTopicAndDetectsDeadClients() {
        RecordingHub hub = new RecordingHub();
        SseEmitter ofA = hub.subscribe(this.orgA, "readings");
        SseEmitter deadOfB = hub.subscribe(this.orgB, "presence");
        hub.dead.add(deadOfB);

        hub.heartbeat();

        assertThat(hub.writes).containsExactly(ofA);
        assertThat(hub.activeCount(this.orgA, "readings")).isEqualTo(1);
        assertThat(hub.activeCount(this.orgB, "presence")).isZero();
    }
}
