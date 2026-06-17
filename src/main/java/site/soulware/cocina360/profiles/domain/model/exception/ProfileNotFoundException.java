package site.soulware.cocina360.profiles.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class ProfileNotFoundException extends EntityNotFoundException {

    private ProfileNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static ProfileNotFoundException byId(UUID id) {
        return new ProfileNotFoundException("error.profile.not_found_by_id", id);
    }

    public static ProfileNotFoundException byEmail(String email) {
        return new ProfileNotFoundException("error.profile.not_found_by_email", email);
    }
}
