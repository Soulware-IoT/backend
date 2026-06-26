package site.soulware.cocina360.internalcontrol.application.controlregistry;

import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlRegistry;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ControlRegistryResult(
        UUID id,
        UUID formatId,
        Map<String, Object> data,
        Instant createdAt
) {

    public static ControlRegistryResult from(ControlRegistry registry) {
        return new ControlRegistryResult(
                registry.getId().value(),
                registry.getFormatId().value(),
                registry.getData(),
                registry.getCreatedAt()
        );
    }
}
