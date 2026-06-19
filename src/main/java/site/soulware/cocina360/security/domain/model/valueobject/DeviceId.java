package site.soulware.cocina360.security.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import site.soulware.cocina360.shared.domain.model.valueobject.AggregateId;

import java.util.UUID;

@Embeddable
public final class DeviceId extends AggregateId {

    public DeviceId(UUID value) {
        super(value);
    }

    public static DeviceId of(UUID value) {
        return new DeviceId(value);
    }

    public static DeviceId generate() {
        return new DeviceId(UUID.randomUUID());
    }
}
