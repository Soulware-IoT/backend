package site.soulware.cocina360.shared.domain.model.valueobject;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public abstract class EntityId implements ValueObject {

    private final UUID value;

    protected EntityId(UUID value) {
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
        EntityId that = (EntityId) o;
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
