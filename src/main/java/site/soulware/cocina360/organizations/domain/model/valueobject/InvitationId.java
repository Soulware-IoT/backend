package site.soulware.cocina360.organizations.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import site.soulware.cocina360.shared.domain.model.valueobject.AggregateId;

import java.util.UUID;

@Embeddable
public final class InvitationId extends AggregateId {

    public InvitationId(UUID value) {
        super(value);
    }

    public static InvitationId of(UUID value) {
        return new InvitationId(value);
    }

    public static InvitationId generate() {
        return new InvitationId(UUID.randomUUID());
    }
}
