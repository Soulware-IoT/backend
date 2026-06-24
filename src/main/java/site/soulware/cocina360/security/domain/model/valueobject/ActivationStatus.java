package site.soulware.cocina360.security.domain.model.valueobject;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Lifecycle state shared by registered hardware (edge devices and devices).
 * An {@code INACTIVE} entity is known to the registry but must not be trusted:
 * the edge stops honouring its credentials and stops accepting its telemetry.
 */
public enum ActivationStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String label;

    ActivationStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String label() {
        return this.label;
    }
}
