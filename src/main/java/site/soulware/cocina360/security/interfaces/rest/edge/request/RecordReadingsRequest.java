package site.soulware.cocina360.security.interfaces.rest.edge.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import site.soulware.cocina360.security.domain.model.command.RecordReadingsCommand;
import site.soulware.cocina360.security.domain.model.valueobject.SafetySeverity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * A batch of safety readings forwarded by an authenticated edge. Each reading references
 * its device by {@code deviceCode} and carries the severity the edge computed locally from
 * the device's thresholds. The organization is resolved from the edge's API key, not the
 * body.
 */
public record RecordReadingsRequest(
    @NotEmpty List<@Valid Reading> readings
) {

    public record Reading(
        @NotBlank String deviceCode,
        int temperatureC,
        double gasPpm,
        @NotNull SafetySeverity severity,
        @NotNull Instant occurredAt
    ) {}

    public RecordReadingsCommand toCommand(UUID organizationId) {
        List<RecordReadingsCommand.Entry> entries = this.readings.stream()
                .map(reading -> new RecordReadingsCommand.Entry(
                        reading.deviceCode(),
                        reading.temperatureC(),
                        reading.gasPpm(),
                        reading.severity(),
                        reading.occurredAt()))
                .toList();
        return new RecordReadingsCommand(organizationId, entries);
    }
}
