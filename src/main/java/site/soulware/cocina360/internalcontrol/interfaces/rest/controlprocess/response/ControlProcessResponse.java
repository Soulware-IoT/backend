package site.soulware.cocina360.internalcontrol.interfaces.rest.controlprocess.response;

import site.soulware.cocina360.internalcontrol.application.controlprocess.ControlProcessResult;

import java.time.Instant;
import java.util.UUID;

public record ControlProcessResponse(
        UUID id,
        UUID organizationId,
        String name,
        Instant createdAt,
        Instant updatedAt
) {

    public static ControlProcessResponse from(ControlProcessResult result) {
        return new ControlProcessResponse(
                result.id(),
                result.organizationId(),
                result.name(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
