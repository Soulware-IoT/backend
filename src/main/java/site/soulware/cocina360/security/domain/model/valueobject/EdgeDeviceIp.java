package site.soulware.cocina360.security.domain.model.valueobject;

import site.soulware.cocina360.shared.domain.model.valueobject.ValueObject;

public record EdgeDeviceIp(String value) implements ValueObject {

    public EdgeDeviceIp {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Edge device IP must not be blank");
        value = value.trim();
    }

    public static EdgeDeviceIp of(String value) {
        return new EdgeDeviceIp(value);
    }
}
