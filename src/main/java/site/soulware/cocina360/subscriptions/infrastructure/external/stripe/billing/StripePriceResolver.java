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

    /**
     * Reverse of {@link #priceIdFor(SubscriptionPlan)} — resolves the paid plan a Stripe price maps to.
     * Returns {@code null} for an unrecognized price (FREE has no price, so it is never returned here).
     */
    public SubscriptionPlan planForPrice(String priceId) {
        if (this.basicPriceId.equals(priceId)) return SubscriptionPlan.BASIC;
        if (this.professionalPriceId.equals(priceId)) return SubscriptionPlan.PROFESSIONAL;
        return null;
    }
}
