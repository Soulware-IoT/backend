package site.soulware.cocina360.organizations.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class OrganizationNotFoundException extends EntityNotFoundException {

    private OrganizationNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static OrganizationNotFoundException byId(UUID id) {
        return new OrganizationNotFoundException("error.organization.not_found_by_id", id);
    }
}
