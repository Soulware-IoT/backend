package site.soulware.cocina360.shared.domain.model.valueobject;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public final class OrganizationId extends AggregateId {

    public OrganizationId(UUID value) {
        super(value);
    }

    public static OrganizationId of(UUID value) {
        return new OrganizationId(value);
    }

    public static OrganizationId generate() {
        return new OrganizationId(UUID.randomUUID());
    }
}
