package site.soulware.cocina360.shared.domain.model.aggregate;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import site.soulware.cocina360.shared.domain.model.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all aggregate roots in the domain. Aggregates are the
 * consistency boundaries: all invariants are enforced within a single aggregate,
 * and cross-aggregate communication happens via domain events.
 *
 * <p>Domain events are collected during the aggregate's lifecycle and published
 * by Spring Data after the repository save completes (via Spring Modulith's
 * event publication mechanism).
 */
@MappedSuperclass
public abstract class AggregateRoot<ID> extends Entity<ID> {

    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }
}
