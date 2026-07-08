package site.soulware.cocina360.security.interfaces.rest.reading.response;

import site.soulware.cocina360.security.domain.model.event.ReadingRecorded;
import site.soulware.cocina360.security.domain.model.valueobject.SafetySeverity;

import java.time.Instant;
import java.util.UUID;

/**
 * Payload of a {@code reading} SSE event pushed to an organization's stream
 * subscribers. Carries the {@code deviceId} so clients can filter per device.
 */
public record ReadingStreamResponse(
    UUID id,
    UUID deviceId,
    int temperatureC,
    double gasPpm,
    SafetySeverity severity,
    Instant occurredAt,
    Instant recordedAt
) {

    public static ReadingStreamResponse from(ReadingRecorded event) {
        return new ReadingStreamResponse(
                event.readingId(),
                event.deviceId(),
                event.temperatureC(),
                event.gasPpm(),
                event.severity(),
                event.occurredAt(),
                event.occurredOn());
    }
}
