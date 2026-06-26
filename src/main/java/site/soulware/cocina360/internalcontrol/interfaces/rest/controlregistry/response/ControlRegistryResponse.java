package site.soulware.cocina360.internalcontrol.interfaces.rest.controlregistry.response;

import site.soulware.cocina360.internalcontrol.application.controlregistry.ControlRegistryResult;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ControlRegistryResponse(
        UUID id,
        UUID formatId,
        Map<String, Object> data,
        Instant createdAt
) {

    public static ControlRegistryResponse from(ControlRegistryResult result) {
        return new ControlRegistryResponse(
                result.id(),
                result.formatId(),
                result.data(),
                result.createdAt()
        );
    }
}
