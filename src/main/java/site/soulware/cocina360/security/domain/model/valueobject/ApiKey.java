package site.soulware.cocina360.security.domain.model.valueobject;

import site.soulware.cocina360.shared.domain.model.valueobject.ValueObject;

import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * A provisioned secret credential. Both the edge gateway (to authenticate to
 * this backend) and a device (to authenticate to its edge) carry one. The
 * backend is the source of truth: it {@link #generate() generates} the key on
 * registration and {@link #generate() rotates} it on demand; downstream the
 * edge replicates the value to enforce authentication at its own boundary.
 */
public record ApiKey(String value) implements ValueObject {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int KEY_BYTES = 32;

    public ApiKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("API key must not be blank");
        }
    }

    public static ApiKey of(String value) {
        return new ApiKey(value);
    }

    public static ApiKey generate() {
        byte[] bytes = new byte[KEY_BYTES];
        RANDOM.nextBytes(bytes);
        return new ApiKey(HexFormat.of().formatHex(bytes));
    }
}
