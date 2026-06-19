package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;

import java.util.UUID;

public class IoTDeviceNotFoundException extends EntityNotFoundException {

    private IoTDeviceNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }

    public static IoTDeviceNotFoundException byId(UUID id) {
        return new IoTDeviceNotFoundException("error.device.not_found_by_id", id);
    }

    public static IoTDeviceNotFoundException byCode(String code) {
        return new IoTDeviceNotFoundException("error.device.not_found_by_code", code);
    }
}
