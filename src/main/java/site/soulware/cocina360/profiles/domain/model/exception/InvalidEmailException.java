package site.soulware.cocina360.profiles.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.DomainException;

public class InvalidEmailException extends DomainException {

    public InvalidEmailException(String value) {
        super("Invalid email: '%s'".formatted(value));
    }
}
