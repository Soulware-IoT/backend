package site.soulware.cocina360.internalcontrol.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import site.soulware.cocina360.shared.domain.model.valueobject.AggregateId;

import java.util.UUID;

@Embeddable
public final class ControlFormatId extends AggregateId {

    public ControlFormatId(UUID value) {
        super(value);
    }

    public static ControlFormatId of(UUID value) {
        return new ControlFormatId(value);
    }

    public static ControlFormatId generate() {
        return new ControlFormatId(UUID.randomUUID());
    }
}
