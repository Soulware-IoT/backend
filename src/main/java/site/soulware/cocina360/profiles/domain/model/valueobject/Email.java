package site.soulware.cocina360.profiles.domain.model.valueobject;

import site.soulware.cocina360.profiles.domain.model.exception.InvalidEmailException;
import site.soulware.cocina360.shared.domain.model.valueobject.ValueObject;

import java.util.regex.Pattern;

public record Email(String value) implements ValueObject {

    private static final Pattern PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("Email must not be blank");
        }
        value = value.trim().toLowerCase();
        if (!PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException(value);
        }
    }
}
