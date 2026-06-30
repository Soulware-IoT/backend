package site.soulware.cocina360.subscriptions.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class SubscriptionNotFoundException extends EntityNotFoundException {

    private SubscriptionNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static SubscriptionNotFoundException byOrganizationId(UUID organizationId) {
        return new SubscriptionNotFoundException("error.subscription.not_found_by_organization", organizationId);
    }
}
