package site.soulware.cocina360.shared.domain.model.valueobject;

import jakarta.persistence.Embeddable;

import java.util.UUID;

/**
 * Base class for all aggregate identity value objects. Each aggregate defines
 * its own typed ID by extending this class, preventing accidental mix-up of
 * IDs across different aggregate types.
 *
 * <p>Example:
 * <pre>{@code
 * @Embeddable
 * public record OrderId(UUID value) extends AggregateId {
 *     public OrderId { Objects.requireNonNull(value); }
 *     public static OrderId generate() { return new OrderId(UUID.randomUUID()); }
 * }
 * }</pre>
 */
@Embeddable
public abstract class AggregateId implements ValueObject {

    private final UUID value;

    protected AggregateId(UUID value) {
        if (value == null) throw new IllegalArgumentException("ID value must not be null");
        this.value = value;
    }

    public UUID value() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        AggregateId that = (AggregateId) o;
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
