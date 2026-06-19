package site.soulware.cocina360.organizations.infrastructure.persistence.organizationmember;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;

@Converter(autoApply = true)
public class PermissionLevelConverter implements AttributeConverter<PermissionLevel, String> {

    @Override
    public String convertToDatabaseColumn(PermissionLevel level) {
        return level == null ? null : level.name().toLowerCase();
    }

    @Override
    public PermissionLevel convertToEntityAttribute(String value) {
        return value == null ? null : PermissionLevel.valueOf(value.toUpperCase());
    }
}
