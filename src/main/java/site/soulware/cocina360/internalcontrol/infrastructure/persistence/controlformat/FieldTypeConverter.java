package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FieldType;

@Converter(autoApply = true)
public class FieldTypeConverter implements AttributeConverter<FieldType, String> {

    @Override
    public String convertToDatabaseColumn(FieldType type) {
        return type == null ? null : type.name().toLowerCase();
    }

    @Override
    public FieldType convertToEntityAttribute(String value) {
        return value == null ? null : FieldType.valueOf(value.toUpperCase());
    }
}
