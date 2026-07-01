package site.soulware.cocina360.subscriptions.infrastructure.external.stripe.billing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

@Component
public
class StripePriceResolver {

    @Value("${stripe.prices.basic}")
    private final String basicPriceId = null;

    @Value("${stripe.prices.professional}")
    private final String professionalPriceId = null;

    public String priceIdFor(SubscriptionPlan plan) {
        return switch (plan) {
            case BASIC -> this.basicPriceId;
            case PROFESSIONAL -> this.professionalPriceId;
            case FREE -> throw new IllegalArgumentException("FREE plan has no Stripe price");
        };
    }
}
