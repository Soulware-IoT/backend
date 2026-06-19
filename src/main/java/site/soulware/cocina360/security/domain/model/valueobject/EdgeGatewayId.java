package site.soulware.cocina360.security.domain.model.valueobject;

import jakarta.persistence.Embeddable;
import site.soulware.cocina360.shared.domain.model.valueobject.AggregateId;

import java.util.UUID;

@Embeddable
public final class EdgeGatewayId extends AggregateId {

    public EdgeGatewayId(UUID value) {
        super(value);
    }

    public static EdgeGatewayId of(UUID value) {
        return new EdgeGatewayId(value);
    }

    public static EdgeGatewayId generate() {
        return new EdgeGatewayId(UUID.randomUUID());
    }
}
