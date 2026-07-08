package site.soulware.cocina360.security.domain.model.aggregate;

import site.soulware.cocina360.security.domain.model.event.CriticalReadingDetected;
import site.soulware.cocina360.security.domain.model.event.ReadingRecorded;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.ReadingId;
import site.soulware.cocina360.security.domain.model.valueobject.SafetySeverity;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

import java.time.Instant;

/**
 * A single entry in the safety ledger. The device reports on a fixed ~5s tick and the
 * edge forwards every buffered reading, so the ledger is a continuous telemetry stream
 * — which is also why device liveness can be derived from it (see the presence
 * tracker). Every recording raises {@link ReadingRecorded} for live telemetry
 * consumers; a {@code CRITICAL} reading additionally raises
 * {@link CriticalReadingDetected} to drive alerts/notifications.
 *
 * @param occurredAt when the reading happened at the edge/iot-device
 * @param recordedAt when this backend persisted it
 */
public class Reading extends AggregateRoot<ReadingId> {

    private final ReadingId id;
    private final IoTDeviceId deviceId;
    private final int temperatureC;
    private final double gasPpm;
    private final SafetySeverity severity;
    private final Instant occurredAt;
    private final Instant recordedAt;

    private Reading(
        ReadingId id,
        IoTDeviceId deviceId,
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

    public static Reading record(
        ReadingId id,
        OrganizationId organizationId,
        IoTDeviceId deviceId,
        IoTDeviceCode deviceCode,
        int temperatureC,
        double gasPpm,
        SafetySeverity severity,
        Instant occurredAt
    ) {
        Reading reading = new Reading(
                id, deviceId, temperatureC, gasPpm, severity, occurredAt, Instant.now());
        reading.registerEvent(new ReadingRecorded(
                organizationId.value(), id.value(), deviceId.value(), deviceCode.value(),
                temperatureC, gasPpm, severity, occurredAt, reading.recordedAt));
        if (severity == SafetySeverity.CRITICAL) {
            reading.registerEvent(new CriticalReadingDetected(
                    id.value(), deviceId.value(), temperatureC, gasPpm, reading.recordedAt));
        }
        return reading;
    }

    public static Reading rehydrate(
        ReadingId id,
        IoTDeviceId deviceId,
        int temperatureC,
        double gasPpm,
        SafetySeverity severity,
        Instant occurredAt,
        Instant recordedAt
    ) {
        return new Reading(id, deviceId, temperatureC, gasPpm, severity, occurredAt, recordedAt);
    }

    @Override
    public ReadingId getId() { return this.id; }
    public IoTDeviceId getDeviceId() { return this.deviceId; }
    public int getTemperatureC() { return this.temperatureC; }
    public double getGasPpm() { return this.gasPpm; }
    public SafetySeverity getSeverity() { return this.severity; }
    public Instant getOccurredAt() { return this.occurredAt; }
    public Instant getRecordedAt() { return this.recordedAt; }
}
