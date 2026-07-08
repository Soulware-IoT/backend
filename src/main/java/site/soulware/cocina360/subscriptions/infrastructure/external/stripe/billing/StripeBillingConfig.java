package site.soulware.cocina360.subscriptions.infrastructure.external.stripe.billing;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeBillingConfig {

    @Value("${stripe.secret-key}")
    private final String secretKey = null;

    @PostConstruct
    void configureStripeApiKey() {
        Stripe.apiKey = this.secretKey;
    }
}
