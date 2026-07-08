package site.soulware.cocina360.organizations.infrastructure.persistence.authz;

/**
 * Read projection of a member's per-area permission levels (lowercase {@code permission_level}
 * labels as stored). Backs the authorization hot path — a thin row, not the member aggregate.
 */
public interface MemberPermissionRow {
    String getSecurity();
    String getOrganizations();
    String getInternalControl();
}
