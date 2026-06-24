package site.soulware.cocina360.security.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import site.soulware.cocina360.shared.domain.model.valueobject.AggregateId;

import java.util.UUID;

@Embeddable
public final class EdgeDeviceId extends AggregateId {

    public EdgeDeviceId(UUID value) {
        super(value);
    }

    public static EdgeDeviceId of(UUID value) {
        return new EdgeDeviceId(value);
    }

    public static EdgeDeviceId generate() {
        return new EdgeDeviceId(UUID.randomUUID());
    }
}
