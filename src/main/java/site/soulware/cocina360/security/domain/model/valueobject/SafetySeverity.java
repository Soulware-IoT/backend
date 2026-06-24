package site.soulware.cocina360.security.domain.model.valueobject;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The hierarchical safety level a reading represents, mirroring the device's
 * traffic-light logic:
 * <ul>
 *   <li>{@code SAFE} — gas and temperature within configured thresholds (green).</li>
 *   <li>{@code WARNING} — a warning threshold crossed; ventilate (yellow).</li>
 *   <li>{@code CRITICAL} — a critical threshold exceeded; evacuate (red + alarm).</li>
 * </ul>
 */
public enum SafetySeverity {
    SAFE("safe"),
    WARNING("warning"),
    CRITICAL("critical");

    private final String label;

    SafetySeverity(String label) {
        this.label = label;
    }

    @JsonValue
    public String label() {
        return this.label;
    }
}
