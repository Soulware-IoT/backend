package site.soulware.cocina360.security.interfaces.rest.reading;

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
 * In-memory registry of the live SSE subscriptions, keyed by organization. Controllers
 * add subscribers via {@link #subscribe(UUID)}; the reading listener pushes events to an
 * organization's subscribers via {@link #broadcast(UUID, String, Object)}.
 *
 * <p>Emitters are created with no timeout; liveness is handled by the 15s heartbeat
 * comment — sending to a client that went away throws, which removes the emitter. All
 * sends go through one guarded path so a dead subscriber never aborts delivery to the
 * rest, and sends are synchronized per emitter because {@link SseEmitter#send} does not
 * support concurrent interleaving (heartbeat thread vs. event listener thread).
 */
@Component
class ReadingSseRegistry {

    private static final Logger log = LoggerFactory.getLogger(ReadingSseRegistry.class);

    private final Map<UUID, CopyOnWriteArrayList<SseEmitter>> emittersByOrganization =
            new ConcurrentHashMap<>();

    SseEmitter subscribe(UUID organizationId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitter.onCompletion(() -> this.remove(organizationId, emitter));
        emitter.onTimeout(() -> this.remove(organizationId, emitter));
        emitter.onError(ex -> this.remove(organizationId, emitter));

        List<SseEmitter> emitters = this.emittersByOrganization
                .computeIfAbsent(organizationId, key -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);
        log.info("SSE subscriber added for org {} ({} active)", organizationId, emitters.size());
        return emitter;
    }

    void broadcast(UUID organizationId, String eventName, Object payload) {
        List<SseEmitter> emitters = this.emittersByOrganization.get(organizationId);
        if (emitters == null) {
            return;
        }
        for (SseEmitter emitter : emitters) {
            this.send(
                    organizationId,
                    emitter,
                    SseEmitter.event().name(eventName).data(payload, MediaType.APPLICATION_JSON));
        }
    }

    @Scheduled(fixedRate = 15_000)
    void heartbeat() {
        this.emittersByOrganization.forEach((organizationId, emitters) -> {
            for (SseEmitter emitter : emitters) {
                this.send(organizationId, emitter, SseEmitter.event().comment("hb"));
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
    private void send(UUID organizationId, SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            this.write(emitter, event);
        } catch (IOException | IllegalStateException ex) {
            this.remove(organizationId, emitter);
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

    int activeCount(UUID organizationId) {
        List<SseEmitter> emitters = this.emittersByOrganization.get(organizationId);
        return emitters == null ? 0 : emitters.size();
    }

    private void remove(UUID organizationId, SseEmitter emitter) {
        List<SseEmitter> emitters = this.emittersByOrganization.get(organizationId);
        if (emitters != null && emitters.remove(emitter)) {
            log.info(
                    "SSE subscriber removed for org {} ({} remaining)",
                    organizationId,
                    emitters.size());
        }
    }
}
