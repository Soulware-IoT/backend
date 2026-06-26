package site.soulware.cocina360.internalcontrol.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import site.soulware.cocina360.shared.domain.model.valueobject.AggregateId;

import java.util.UUID;

@Embeddable
public final class ControlProcessId extends AggregateId {

    public ControlProcessId(UUID value) {
        super(value);
    }

    public static ControlProcessId of(UUID value) {
        return new ControlProcessId(value);
    }

    public static ControlProcessId generate() {
        return new ControlProcessId(UUID.randomUUID());
    }
}
