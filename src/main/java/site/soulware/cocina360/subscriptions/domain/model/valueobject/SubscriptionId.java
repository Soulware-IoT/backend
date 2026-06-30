package site.soulware.cocina360.subscriptions.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import site.soulware.cocina360.shared.domain.model.valueobject.AggregateId;

import java.util.UUID;

@Embeddable
public final class SubscriptionId extends AggregateId {

    public SubscriptionId(UUID value) {
        super(value);
    }

    public static SubscriptionId of(UUID value) {
        return new SubscriptionId(value);
    }

    public static SubscriptionId generate() {
        return new SubscriptionId(UUID.randomUUID());
    }
}
