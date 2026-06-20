package site.soulware.cocina360.security.infrastructure.persistence.reading.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import site.soulware.cocina360.security.domain.model.valueobject.SafetySeverity;

import java.time.Instant;
import java.util.UUID;

/**
 * MongoDB document mapping for a {@link site.soulware.cocina360.security.domain.model.aggregate.Reading}.
 * The {@code Reading} ledger is the one aggregate stored in MongoDB rather than Postgres
 * (high-volume safety telemetry forwarded from the edge); see CLAUDE.md "Polyglot
 * persistence". {@code deviceId} is indexed to support {@code findByDeviceId} lookups.
 */
@Document(collection = "readings")
public class ReadingDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID deviceId;

    private int temperatureC;
    private double gasPpm;
    private SafetySeverity severity;
    private Instant occurredAt;
    private Instant recordedAt;

    protected ReadingDocument() {
    }

    public ReadingDocument(
        UUID id,
        UUID deviceId,
        int temperatureC,
        double gasPpm,
        SafetySeverity severity,
        Instant occurredAt,
        Instant recordedAt
    ) {
        this.id = id;
        this.deviceId = deviceId;
        this.temperatureC = temperatureC;
        this.gasPpm = gasPpm;
        this.severity = severity;
        this.occurredAt = occurredAt;
        this.recordedAt = recordedAt;
    }

    public UUID getId() { return this.id; }
    public UUID getDeviceId() { return this.deviceId; }
    public int getTemperatureC() { return this.temperatureC; }
    public double getGasPpm() { return this.gasPpm; }
    public SafetySeverity getSeverity() { return this.severity; }
    public Instant getOccurredAt() { return this.occurredAt; }
    public Instant getRecordedAt() { return this.recordedAt; }
}
