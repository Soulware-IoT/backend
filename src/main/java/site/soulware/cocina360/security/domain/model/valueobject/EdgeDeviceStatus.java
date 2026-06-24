package site.soulware.cocina360.security.domain.model.valueobject;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The lifecycle of an edge device:
 * <ul>
 *   <li>{@code PROVISIONED} — minted at the factory with a code + apiKey, not yet
 *       claimed by an organization. Known to the registry but not in service.</li>
 *   <li>{@code ACTIVE} — claimed by an organization; in service.</li>
 *   <li>{@code INACTIVE} — claimed but disabled; the backend stops trusting its key.</li>
 * </ul>
 */
public enum EdgeDeviceStatus {
    PROVISIONED("provisioned"),
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String label;

    EdgeDeviceStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String label() {
        return this.label;
    }
}
