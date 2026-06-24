package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class EdgeDeviceNotFoundException extends EntityNotFoundException {

    private EdgeDeviceNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static EdgeDeviceNotFoundException byId(UUID id) {
        return new EdgeDeviceNotFoundException("error.edge_device.not_found_by_id", id);
    }

    public static EdgeDeviceNotFoundException byOrganizationId(UUID organizationId) {
        return new EdgeDeviceNotFoundException("error.edge_device.not_found_by_organization", organizationId);
    }

    public static EdgeDeviceNotFoundException byCode(String code) {
        return new EdgeDeviceNotFoundException("error.edge_device.not_found_by_code", code);
    }
}
