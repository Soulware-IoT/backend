package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatStatus;

@Converter(autoApply = true)
public class ControlFormatStatusConverter implements AttributeConverter<ControlFormatStatus, String> {

    @Override
    public String convertToDatabaseColumn(ControlFormatStatus status) {
        return status == null ? null : status.name().toLowerCase();
    }

    @Override
    public ControlFormatStatus convertToEntityAttribute(String value) {
        return value == null ? null : ControlFormatStatus.valueOf(value.toUpperCase());
    }
}
