package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class EdgeGatewayNotFoundException extends EntityNotFoundException {

    private EdgeGatewayNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static EdgeGatewayNotFoundException byId(UUID id) {
        return new EdgeGatewayNotFoundException("error.edge_gateway.not_found_by_id", id);
    }

    public static EdgeGatewayNotFoundException byOrganizationId(UUID organizationId) {
        return new EdgeGatewayNotFoundException("error.edge_gateway.not_found_by_organization", organizationId);
    }
}
