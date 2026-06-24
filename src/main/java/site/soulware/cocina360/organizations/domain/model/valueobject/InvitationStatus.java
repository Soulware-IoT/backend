package site.soulware.cocina360.organizations.domain.model.valueobject;

import com.fasterxml.jackson.annotation.JsonValue;

public enum InvitationStatus {
    PENDING("pending"),
    ACCEPTED("accepted"),
    DECLINED("declined");

    private final String label;

    InvitationStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String label() {
        return this.label;
    }
}
