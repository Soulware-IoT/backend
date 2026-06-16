package site.soulware.cocina360.shared.domain.model.valueobject;

/**
 * Marker interface for value objects. Value objects are immutable, structurally
 * equal, and have no identity of their own. Implementors must:
 * <ul>
 *   <li>Be immutable (all fields final, no setters).</li>
 *   <li>Implement equals/hashCode based solely on their fields.</li>
 *   <li>Enforce their own invariants in the constructor.</li>
 * </ul>
 *
 * <p>Prefer Java {@code record} types — they satisfy all requirements above
 * automatically. Use {@code @Embeddable} when mapped to a JPA column.
 */
public interface ValueObject {
}
