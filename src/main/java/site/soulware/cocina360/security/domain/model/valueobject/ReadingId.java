package site.soulware.cocina360.security.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import site.soulware.cocina360.shared.domain.model.valueobject.AggregateId;

import java.util.UUID;

@Embeddable
public final class ReadingId extends AggregateId {

    public ReadingId(UUID value) {
        super(value);
    }

    public static ReadingId of(UUID value) {
        return new ReadingId(value);
    }

    public static ReadingId generate() {
        return new ReadingId(UUID.randomUUID());
    }
}
