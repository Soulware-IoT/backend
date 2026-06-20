package site.soulware.cocina360.security.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import site.soulware.cocina360.shared.domain.model.valueobject.AggregateId;

import java.util.UUID;

@Embeddable
public final class IoTDeviceId extends AggregateId {

    public IoTDeviceId(UUID value) {
        super(value);
    }

    public static IoTDeviceId of(UUID value) {
        return new IoTDeviceId(value);
    }

    public static IoTDeviceId generate() {
        return new IoTDeviceId(UUID.randomUUID());
    }
}
