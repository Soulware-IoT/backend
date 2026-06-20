package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class ReadingNotFoundException extends EntityNotFoundException {

    private ReadingNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static ReadingNotFoundException byId(UUID id) {
        return new ReadingNotFoundException("error.reading.not_found_by_id", id);
    }
}
