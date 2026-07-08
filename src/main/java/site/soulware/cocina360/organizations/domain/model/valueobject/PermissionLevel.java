package site.soulware.cocina360.organizations.domain.model.valueobject;

public enum PermissionLevel {
    ADMIN,
    LIEUTENANT,
    ASSIGNEE,
    NONE;

    /** Ascending rank so a higher level outranks every lower one ({@code NONE} = 0, {@code ADMIN} = 3). */
    public int rank() {
        return switch (this) {
            case NONE -> 0;
            case ASSIGNEE -> 1;
            case LIEUTENANT -> 2;
            case ADMIN -> 3;
        };
    }

    /** Whether this level is strictly below {@code other} — i.e. {@code other} may grant it. */
    public boolean isBelow(PermissionLevel other) {
        return this.rank() < other.rank();
    }
}
