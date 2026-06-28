package site.soulware.cocina360.organizations.interfaces.acl;

import org.springframework.stereotype.Service;
import site.soulware.cocina360.organizations.application.authorization.AuthorizationQueryService;
import site.soulware.cocina360.organizations.application.authorization.MemberLevels;
import site.soulware.cocina360.organizations.domain.model.exception.InsufficientPermissionException;
import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;

import java.util.UUID;

/**
 * {@link AuthorizationApi} adapter: delegates to the {@link AuthorizationQueryService} (which
 * reads levels live from the DB) and maps the internal {@link PermissionLevel} to the published
 * {@link AccessLevel}, selecting the column for the requested {@link PermissionArea}.
 */
@Service
class AuthorizationApiImpl implements AuthorizationApi {

    private final AuthorizationQueryService queryService;

    AuthorizationApiImpl(AuthorizationQueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    public void requirePermission(UUID organizationId, UUID profileId, PermissionArea area, AccessLevel minimum) {
        if (!this.currentLevel(organizationId, profileId, area).satisfies(minimum)) {
            throw new InsufficientPermissionException();
        }
    }

    @Override
    public AccessLevel currentLevel(UUID organizationId, UUID profileId, PermissionArea area) {
        return this.queryService.findLevels(organizationId, profileId)
                .map(levels -> map(forArea(levels, area)))
                .orElse(AccessLevel.NONE);
    }

    private static PermissionLevel forArea(MemberLevels levels, PermissionArea area) {
        return switch (area) {
            case SECURITY -> levels.security();
            case ORGANIZATIONS -> levels.organizations();
            case INTERNAL_CONTROL -> levels.internalControl();
        };
    }

    private static AccessLevel map(PermissionLevel level) {
        return AccessLevel.valueOf(level.name());
    }
}
