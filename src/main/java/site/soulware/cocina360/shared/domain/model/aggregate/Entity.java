package site.soulware.cocina360.shared.domain.model.aggregate;

import jakarta.persistence.MappedSuperclass;

import java.util.Objects;

/**
 * Base class for domain entities. Identity-based equality: two entities are
 * equal if and only if their IDs are equal, regardless of other field values.
 */
@MappedSuperclass
public abstract class Entity<ID> {

    public abstract ID getId();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return getId() != null && Objects.equals(getId(), entity.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? Objects.hashCode(getId()) : System.identityHashCode(this);
    }
}
