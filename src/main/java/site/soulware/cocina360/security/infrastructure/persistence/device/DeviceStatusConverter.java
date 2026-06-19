package site.soulware.cocina360.security.infrastructure.persistence.device;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceStatus;

@Converter(autoApply = true)
public class DeviceStatusConverter implements AttributeConverter<DeviceStatus, String> {

    @Override
    public String convertToDatabaseColumn(DeviceStatus status) {
        return status == null ? null : status.name().toLowerCase();
    }

    @Override
    public DeviceStatus convertToEntityAttribute(String value) {
        return value == null ? null : DeviceStatus.valueOf(value.toUpperCase());
    }
}
