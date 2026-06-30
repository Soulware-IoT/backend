package site.soulware.cocina360.subscriptions.infrastructure.persistence.subscription.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

@Converter(autoApply = true)
public class SubscriptionPlanConverter implements AttributeConverter<SubscriptionPlan, String> {

    @Override
    public String convertToDatabaseColumn(SubscriptionPlan plan) {
        return plan == null ? null : plan.name().toLowerCase();
    }

    @Override
    public SubscriptionPlan convertToEntityAttribute(String value) {
        return value == null ? null : SubscriptionPlan.valueOf(value.toUpperCase());
    }
}
