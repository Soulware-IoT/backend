package site.soulware.cocina360.internalcontrol.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import site.soulware.cocina360.shared.domain.model.valueobject.EntityId;

import java.util.UUID;

@Embeddable
public final class FormatFieldId extends EntityId {

    public FormatFieldId(UUID value) {
        super(value);
    }

    public static FormatFieldId of(UUID value) {
        return new FormatFieldId(value);
    }

    public static FormatFieldId generate() {
        return new FormatFieldId(UUID.randomUUID());
    }
}
