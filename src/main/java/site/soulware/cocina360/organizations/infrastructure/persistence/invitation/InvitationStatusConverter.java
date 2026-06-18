package site.soulware.cocina360.organizations.infrastructure.persistence.invitation;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationStatus;

@Converter(autoApply = true)
public class InvitationStatusConverter implements AttributeConverter<InvitationStatus, String> {

    @Override
    public String convertToDatabaseColumn(InvitationStatus status) {
        return status == null ? null : status.name().toLowerCase();
    }

    @Override
    public InvitationStatus convertToEntityAttribute(String value) {
        return value == null ? null : InvitationStatus.valueOf(value.toUpperCase());
    }
}
