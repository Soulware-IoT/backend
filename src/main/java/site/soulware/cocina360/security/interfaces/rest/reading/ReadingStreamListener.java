package site.soulware.cocina360.security.interfaces.rest.reading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import site.soulware.cocina360.security.domain.model.event.ReadingRecorded;
import site.soulware.cocina360.security.interfaces.rest.reading.response.ReadingStreamResponse;
import site.soulware.cocina360.security.interfaces.rest.sse.OrganizationSseHub;

/**
 * Fans a {@link ReadingRecorded} out to the organization's live SSE subscribers after
 * the recording transaction commits.
 *
 * <p>Uses {@code @ApplicationModuleListener} (transactional, async, ordered) so delivery
 * is decoupled from ingestion — a slow or disconnecting subscriber never blocks or rolls
 * back the reading write.
 */
@Component
class ReadingStreamListener {

    private static final Logger log = LoggerFactory.getLogger(ReadingStreamListener.class);

    private static final String READING_EVENT = "reading";

    private final OrganizationSseHub hub;

    ReadingStreamListener(OrganizationSseHub hub) {
        this.hub = hub;
    }

    @ApplicationModuleListener
    void on(ReadingRecorded event) {
        log.debug(
                "Reading {} recorded for device {} (org {}): {}°C / {} ppm [{}]",
                event.readingId(),
                event.deviceId(),
                event.organizationId(),
                event.temperatureC(),
                event.gasPpm(),
                event.severity());
        this.hub.broadcast(
                event.organizationId(),
                ReadingStreamController.TOPIC,
                READING_EVENT,
                ReadingStreamResponse.from(event));
    }
}
