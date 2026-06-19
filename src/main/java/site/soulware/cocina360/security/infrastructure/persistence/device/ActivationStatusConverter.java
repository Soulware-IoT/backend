package site.soulware.cocina360.security.infrastructure.persistence.device;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.soulware.cocina360.security.domain.model.valueobject.ActivationStatus;

@Converter(autoApply = true)
public class ActivationStatusConverter implements AttributeConverter<ActivationStatus, String> {

    @Override
    public String convertToDatabaseColumn(ActivationStatus status) {
        return status == null ? null : status.name().toLowerCase();
    }

    @Override
    public ActivationStatus convertToEntityAttribute(String value) {
        return value == null ? null : ActivationStatus.valueOf(value.toUpperCase());
    }
}
