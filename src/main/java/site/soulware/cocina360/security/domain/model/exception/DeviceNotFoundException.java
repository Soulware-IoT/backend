package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class DeviceNotFoundException extends EntityNotFoundException {

    private DeviceNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static DeviceNotFoundException byId(UUID id) {
        return new DeviceNotFoundException("error.device.not_found_by_id", id);
    }

    public static DeviceNotFoundException byCode(String code) {
        return new DeviceNotFoundException("error.device.not_found_by_code", code);
    }
}
