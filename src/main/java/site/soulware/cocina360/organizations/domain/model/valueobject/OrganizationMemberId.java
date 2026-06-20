package site.soulware.cocina360.organizations.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import site.soulware.cocina360.shared.domain.model.valueobject.AggregateId;

import java.util.UUID;

@Embeddable
public final class OrganizationMemberId extends AggregateId {

    public OrganizationMemberId(UUID value) {
        super(value);
    }

    public static OrganizationMemberId of(UUID value) {
        return new OrganizationMemberId(value);
    }

    public static OrganizationMemberId generate() {
        return new OrganizationMemberId(UUID.randomUUID());
    }
}
