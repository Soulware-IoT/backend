package site.soulware.cocina360.security.domain.model.aggregate;

import site.soulware.cocina360.security.domain.model.event.CriticalReadingDetected;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.ReadingId;
import site.soulware.cocina360.security.domain.model.valueobject.SafetySeverity;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;

import java.time.Instant;

/**
 * A single entry in the sparse safety ledger. Readings are not the device's 5s
 * tick — the edge forwards one only when the safety state changes, so each row is
 * a meaningful transition. A {@code CRITICAL} reading raises
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
        IoTDeviceId deviceId,
        int temperatureC,
        double gasPpm,
        SafetySeverity severity,
        Instant occurredAt
    ) {
        Reading reading = new Reading(
                id, deviceId, temperatureC, gasPpm, severity, occurredAt, Instant.now());
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
