package site.soulware.cocina360.security.domain.model.valueobject;

import site.soulware.cocina360.shared.domain.model.valueobject.ValueObject;

/**
 * The natural hardware identifier a physical device reports to the edge and the
 * backend (e.g. {@code ESP32_COCINA_01}). Stable across the device's lifetime
 * and unique within the fleet; distinct from the surrogate {@link DeviceId}.
 */
public record DeviceCode(String value) implements ValueObject {

    public DeviceCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Device code must not be blank");
        }
        value = value.trim();
    }

    public static DeviceCode of(String value) {
        return new DeviceCode(value);
    }
}
