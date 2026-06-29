package site.soulware.cocina360.organizations.application.authorization;

import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;

/**
 * A member's per-area permission levels, read for authorization. Plain domain levels — the
 * {@code acl} adapter maps them to the published {@code AccessLevel}.
 */
public record MemberLevels(
    PermissionLevel security,
    PermissionLevel organizations,
    PermissionLevel internalControl
) {
}
