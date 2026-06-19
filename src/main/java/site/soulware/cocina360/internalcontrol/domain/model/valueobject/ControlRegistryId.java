package site.soulware.cocina360.internalcontrol.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import site.soulware.cocina360.shared.domain.model.valueobject.AggregateId;

import java.util.UUID;

@Embeddable
public final class ControlRegistryId extends AggregateId {

    public ControlRegistryId(UUID value) {
        super(value);
    }

    public static ControlRegistryId of(UUID value) {
        return new ControlRegistryId(value);
    }

    public static ControlRegistryId generate() {
        return new ControlRegistryId(UUID.randomUUID());
    }
}
