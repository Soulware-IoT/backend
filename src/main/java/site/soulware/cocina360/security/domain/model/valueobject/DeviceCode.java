package site.soulware.cocina360.security.domain.model.valueobject;

import site.soulware.cocina360.shared.domain.model.valueobject.ValueObject;

import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * The hardware identifier a physical device reports to the edge and the backend
 * (e.g. {@code COCINA-3F9A2B7C}). Minted by the backend at the factory provisioning
 * step and burned into the firmware; stable across the device's lifetime and unique
 * within the fleet; distinct from the surrogate {@link DeviceId}.
 */
public record DeviceCode(String value) implements ValueObject {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String PREFIX = "COCINA-";
    private static final int SUFFIX_BYTES = 4;

    public DeviceCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Device code must not be blank");
        }
        value = value.trim();
    }

    public static DeviceCode of(String value) {
        return new DeviceCode(value);
    }

    /** Mints a new factory code, e.g. {@code COCINA-3F9A2B7C}. */
    public static DeviceCode generate() {
        byte[] bytes = new byte[SUFFIX_BYTES];
        RANDOM.nextBytes(bytes);
        return new DeviceCode(PREFIX + HexFormat.of().withUpperCase().formatHex(bytes));
    }
}
