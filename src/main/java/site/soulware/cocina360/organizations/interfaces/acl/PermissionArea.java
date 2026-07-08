package site.soulware.cocina360.organizations.interfaces.acl;

/**
 * The functional area a permission level applies to within an organization. Part of the
 * {@code organizations} published authorization language (named interface {@code "acl"}), so
 * consuming contexts express which area they are guarding without importing organizations
 * internals.
 */
public enum PermissionArea {
    SECURITY,
    ORGANIZATIONS,
    INTERNAL_CONTROL
}
