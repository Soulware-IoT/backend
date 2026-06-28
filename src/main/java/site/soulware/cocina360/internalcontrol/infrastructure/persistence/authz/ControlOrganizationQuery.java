package site.soulware.cocina360.internalcontrol.infrastructure.persistence.authz;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlprocess.jpa.ControlProcessJpaEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Resolves the owning organization of an internal-control resource for authorization, by id.
 * Intra-context native lookups (internal-control schema only): process → org, format → process →
 * org, registry → format → process → org. The requester's permission in the returned org is then
 * checked via {@code AuthorizationApi}.
 */
public interface ControlOrganizationQuery extends Repository<ControlProcessJpaEntity, UUID> {

    @Query(value = "SELECT organization_id FROM control_processes WHERE id = :id", nativeQuery = true)
    Optional<UUID> findByProcess(@Param("id") UUID processId);

    @Query(value = """
        SELECT p.organization_id
        FROM control_formats f
        JOIN control_processes p ON p.id = f.process_id
        WHERE f.id = :id
        """, nativeQuery = true)
    Optional<UUID> findByFormat(@Param("id") UUID formatId);

    @Query(value = """
        SELECT p.organization_id
        FROM control_registries r
        JOIN control_formats f   ON f.id = r.format_id
        JOIN control_processes p ON p.id = f.process_id
        WHERE r.id = :id
        """, nativeQuery = true)
    Optional<UUID> findByRegistry(@Param("id") UUID registryId);
}
