package site.soulware.cocina360.organizations.interfaces.acl;

/**
 * Ranked authorization level a member holds in an organization area, ordered ascending so a
 * higher rank includes the rights of every lower one. Part of the {@code organizations}
 * published authorization language (named interface {@code "acl"}); mirrors the internal
 * {@code PermissionLevel} but is the type consuming contexts depend on.
 */
public enum AccessLevel {
    NONE,
    ASSIGNEE,
    LIEUTENANT,
    ADMIN;

    /** Whether this level meets or exceeds the required minimum. */
    public boolean satisfies(AccessLevel minimum) {
        return this.ordinal() >= minimum.ordinal();
    }
}
