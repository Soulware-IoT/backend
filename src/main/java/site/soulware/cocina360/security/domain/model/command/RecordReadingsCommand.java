package site.soulware.cocina360.security.domain.model.command;

import site.soulware.cocina360.security.domain.model.valueobject.SafetySeverity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Ingest a batch of safety readings forwarded by an organization's edge. The edge
 * authenticates as its organization and computes each reading's {@link SafetySeverity}
 * locally (it holds the thresholds from the registry pull), so the backend records the
 * severity as reported. Each entry references its device by {@code deviceCode} — the
 * stable hardware id — which the backend resolves to an {@code IoTDevice} within the
 * given organization.
 */
public record RecordReadingsCommand(
    UUID organizationId,
    List<Entry> readings
) {

    public record Entry(
        String deviceCode,
        int temperatureC,
        double gasPpm,
        SafetySeverity severity,
        Instant occurredAt
    ) {}
}
