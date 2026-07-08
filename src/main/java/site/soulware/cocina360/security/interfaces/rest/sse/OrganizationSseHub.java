package site.soulware.cocina360.security.interfaces.rest.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory registry of live SSE subscriptions, keyed by {@code (organizationId, topic)}
 * so independent streams (e.g. readings, device presence) never share subscribers even
 * though they're broadcast to the same organization. Controllers add subscribers via
 * {@link #subscribe(UUID, String)}; feature-specific listeners push events via
 * {@link #broadcast(UUID, String, String, Object)}.
 *
 * <p>Emitters are created with no timeout; liveness is handled by the 15s heartbeat
 * comment — sending to a client that went away throws, which removes the emitter. All
 * sends go through one guarded path so a dead subscriber never aborts delivery to the
 * rest, and sends are synchronized per emitter because {@link SseEmitter#send} does not
 * support concurrent interleaving (heartbeat thread vs. event listener thread).
 */
@Component
public class OrganizationSseHub {

    private static final Logger log = LoggerFactory.getLogger(OrganizationSseHub.class);

    private record Topic(UUID organizationId, String topic) {}

    private final Map<Topic, CopyOnWriteArrayList<SseEmitter>> emittersByTopic =
            new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID organizationId, String topic) {
        Topic key = new Topic(organizationId, topic);
        SseEmitter emitter = new SseEmitter(0L);
        emitter.onCompletion(() -> this.remove(key, emitter));
        emitter.onTimeout(() -> this.remove(key, emitter));
        emitter.onError(ex -> this.remove(key, emitter));

        List<SseEmitter> emitters = this.emittersByTopic
                .computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);
        log.info(
                "SSE subscriber added for org {} topic {} ({} active)",
                organizationId, topic, emitters.size());
        return emitter;
    }

    public void broadcast(UUID organizationId, String topic, String eventName, Object payload) {
        Topic key = new Topic(organizationId, topic);
        List<SseEmitter> emitters = this.emittersByTopic.get(key);
        if (emitters == null) {
            return;
        }
        for (SseEmitter emitter : emitters) {
            this.send(
                    key,
                    emitter,
                    SseEmitter.event().name(eventName).data(payload, MediaType.APPLICATION_JSON));
        }
    }

    @Scheduled(fixedRate = 15_000)
    void heartbeat() {
        this.emittersByTopic.forEach((key, emitters) -> {
            for (SseEmitter emitter : emitters) {
                this.send(key, emitter, SseEmitter.event().comment("hb"));
            }
        });
    }

    /**
     * Single guarded send path: a subscriber whose connection is gone throws here, gets
     * deregistered, and never prevents delivery to the remaining subscribers. The dead
     * emitter is only removed, not completed — completing dispatches the failure back
     * through the MVC exception handlers, which cannot write an error body into an
     * already-committed event-stream response; the container reclaims the request itself.
     */
    private void send(Topic key, SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            this.write(emitter, event);
        } catch (IOException | IllegalStateException ex) {
            this.remove(key, emitter);
        }
    }

    /**
     * Raw write to one emitter, synchronized because {@link SseEmitter#send} does not
     * support concurrent interleaving. Seam overridden in tests.
     */
    void write(SseEmitter emitter, SseEmitter.SseEventBuilder event) throws IOException {
        synchronized (emitter) {
            emitter.send(event);
        }
    }

    public int activeCount(UUID organizationId, String topic) {
        List<SseEmitter> emitters = this.emittersByTopic.get(new Topic(organizationId, topic));
        return emitters == null ? 0 : emitters.size();
    }

    private void remove(Topic key, SseEmitter emitter) {
        List<SseEmitter> emitters = this.emittersByTopic.get(key);
        if (emitters != null && emitters.remove(emitter)) {
            log.info(
                    "SSE subscriber removed for org {} topic {} ({} remaining)",
                    key.organizationId(), key.topic(), emitters.size());
        }
    }
}
