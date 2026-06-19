package site.soulware.cocina360.security.infrastructure.persistence.iotdevice;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceStatus;

@Converter(autoApply = true)
public class IoTDeviceStatusConverter implements AttributeConverter<IoTDeviceStatus, String> {

    @Override
    public String convertToDatabaseColumn(IoTDeviceStatus status) {
        return status == null ? null : status.name().toLowerCase();
    }

    @Override
    public IoTDeviceStatus convertToEntityAttribute(String value) {
        return value == null ? null : IoTDeviceStatus.valueOf(value.toUpperCase());
    }
}
