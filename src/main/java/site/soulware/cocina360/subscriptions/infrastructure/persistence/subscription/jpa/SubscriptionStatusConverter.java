package site.soulware.cocina360.subscriptions.infrastructure.persistence.subscription.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionStatus;

@Converter(autoApply = true)
public class SubscriptionStatusConverter implements AttributeConverter<SubscriptionStatus, String> {

    @Override
    public String convertToDatabaseColumn(SubscriptionStatus status) {
        return status == null ? null : status.name().toLowerCase();
    }

    @Override
    public SubscriptionStatus convertToEntityAttribute(String value) {
        return value == null ? null : SubscriptionStatus.valueOf(value.toUpperCase());
    }
}
