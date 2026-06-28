package site.soulware.cocina360.organizations.infrastructure.persistence.authz;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import site.soulware.cocina360.organizations.infrastructure.persistence.organizationmember.jpa.OrganizationMemberJpaEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Authorization hot-path read: resolves a member's per-area permission levels for an
 * (organization, profile) pair in a single native query, joining the member and its permissions
 * row. Stays within the {@code organizations} schema — never joins another context's tables.
 */
public interface MemberPermissionJpaQuery extends Repository<OrganizationMemberJpaEntity, UUID> {

    @Query(value = """
        SELECT p.security          AS security,
               p.organizations     AS organizations,
               p.internal_control  AS internalControl
        FROM organization_members m
        JOIN organization_member_permissions p ON p.organization_member_id = m.id
        WHERE m.organization_id = :organizationId AND m.profile_id = :profileId
        """, nativeQuery = true)
    Optional<MemberPermissionRow> findLevels(
        @Param("organizationId") UUID organizationId,
        @Param("profileId") UUID profileId
    );
}
