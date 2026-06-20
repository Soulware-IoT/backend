package site.soulware.cocina360.organizations.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class OrganizationMemberNotFoundException extends EntityNotFoundException {

    private OrganizationMemberNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static OrganizationMemberNotFoundException byId(UUID id) {
        return new OrganizationMemberNotFoundException("error.organization_member.not_found_by_id", id);
    }
}
