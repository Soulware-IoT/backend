package site.soulware.cocina360.shared.domain.model.event;

import java.time.Instant;

/**
 * Marker interface for all domain events. Domain events represent something
 * that happened in the domain and are named in past tense.
 *
 * <p>Use Java {@code record} types for concrete event implementations — they are
 * naturally immutable and concise. Spring Modulith will publish events collected
 * from aggregate roots to the application event bus after the transaction commits.
 *
 * <p>Example:
 * <pre>{@code
 * public record OrderPlaced(UUID orderId, Instant occurredOn) implements DomainEvent {}
 * }</pre>
 */
public interface DomainEvent {

    Instant occurredOn();
}
