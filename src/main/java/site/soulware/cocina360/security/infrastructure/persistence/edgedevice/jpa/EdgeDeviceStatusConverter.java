package site.soulware.cocina360.security.infrastructure.persistence.edgedevice.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceStatus;

@Converter(autoApply = true)
public class EdgeDeviceStatusConverter implements AttributeConverter<EdgeDeviceStatus, String> {

    @Override
    public String convertToDatabaseColumn(EdgeDeviceStatus status) {
        return status == null ? null : status.name().toLowerCase();
    }

    @Override
    public EdgeDeviceStatus convertToEntityAttribute(String value) {
        return value == null ? null : EdgeDeviceStatus.valueOf(value.toUpperCase());
    }
}
