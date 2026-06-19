package site.soulware.cocina360.security.domain.model.valueobject;

import site.soulware.cocina360.shared.domain.model.valueobject.ValueObject;

import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * The hardware identifier an edge device reports when it is claimed (e.g.
 * {@code EDGE-3F9A2B7C}). Minted by the backend at the factory provisioning step and
 * burned into the edge's configuration; stable across its lifetime and unique within the
 * fleet; distinct from the surrogate {@link EdgeDeviceId} and from an {@link IoTDeviceCode}.
 */
public record EdgeDeviceCode(String value) implements ValueObject {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String PREFIX = "EDGE-";
    private static final int SUFFIX_BYTES = 4;

    public EdgeDeviceCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Edge device code must not be blank");
        }
        value = value.trim();
    }

    public static EdgeDeviceCode of(String value) {
        return new EdgeDeviceCode(value);
    }

    /** Mints a new factory code, e.g. {@code EDGE-3F9A2B7C}. */
    public static EdgeDeviceCode generate() {
        byte[] bytes = new byte[SUFFIX_BYTES];
        RANDOM.nextBytes(bytes);
        return new EdgeDeviceCode(PREFIX + HexFormat.of().withUpperCase().formatHex(bytes));
    }
}
