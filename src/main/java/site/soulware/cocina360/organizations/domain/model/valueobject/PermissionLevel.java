package site.soulware.cocina360.organizations.domain.model.valueobject;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PermissionLevel {
    ADMIN("admin"),
    LIEUTENANT("lieutenant"),
    ASSIGNEE("assignee"),
    NONE("none");

    private final String label;

    PermissionLevel(String label) {
        this.label = label;
    }

    @JsonValue
    public String label() {
        return this.label;
    }
}
