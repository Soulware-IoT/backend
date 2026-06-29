package site.soulware.cocina360.organizations.application.authorization;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;
import site.soulware.cocina360.organizations.infrastructure.persistence.authz.MemberPermissionJpaQuery;
import site.soulware.cocina360.organizations.infrastructure.persistence.authz.MemberPermissionRow;

import java.util.Optional;
import java.util.UUID;

/**
 * Read side of authorization: resolves a member's permission levels live from the database
 * (anti-staleness) for the {@code AuthorizationApi} facade. Read-only and single-query.
 */
@Service
@Transactional(readOnly = true)
public class AuthorizationQueryService {

    private final MemberPermissionJpaQuery query;

    public AuthorizationQueryService(MemberPermissionJpaQuery query) {
        this.query = query;
    }

    /** The member's per-area levels, or empty if the profile is not a member of the organization. */
    public Optional<MemberLevels> findLevels(UUID organizationId, UUID profileId) {
        return this.query.findLevels(organizationId, profileId).map(AuthorizationQueryService::toLevels);
    }

    private static MemberLevels toLevels(MemberPermissionRow row) {
        return new MemberLevels(
                level(row.getSecurity()),
                level(row.getOrganizations()),
                level(row.getInternalControl()));
    }

    private static PermissionLevel level(String value) {
        return PermissionLevel.valueOf(value.toUpperCase());
    }
}
