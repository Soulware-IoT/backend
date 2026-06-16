package site.soulware.cocina360.shared.domain.repository;

import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;

import java.util.Optional;

/**
 * Marker interface for domain repositories. Each aggregate root has exactly
 * one repository. Repositories are defined in the domain layer (this interface)
 * and implemented in the infrastructure layer (JPA adapters).
 *
 * <p>Domain code depends only on this interface — never on Spring Data
 * repositories or JPA directly.
 *
 * @param <A>  aggregate root type
 * @param <ID> aggregate identity type
 */
public interface DomainRepository<A extends AggregateRoot<ID>, ID> {

    A save(A aggregate);

    Optional<A> findById(ID id);

    void delete(A aggregate);
}
