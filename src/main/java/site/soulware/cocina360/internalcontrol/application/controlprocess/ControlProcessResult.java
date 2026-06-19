package site.soulware.cocina360.internalcontrol.application.controlprocess;

import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlProcess;

import java.time.Instant;
import java.util.UUID;

public record ControlProcessResult(
        UUID id,
        UUID organizationId,
        String name,
        Instant createdAt,
        Instant updatedAt
) {

    public static ControlProcessResult from(ControlProcess process) {
        return new ControlProcessResult(
                process.getId().value(),
                process.getOrganizationId().value(),
                process.getName(),
                process.getCreatedAt(),
                process.getUpdatedAt()
        );
    }
}
