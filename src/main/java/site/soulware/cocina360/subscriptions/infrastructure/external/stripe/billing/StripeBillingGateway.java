package site.soulware.cocina360.subscriptions.infrastructure.external.stripe.billing;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionUpdateParams;
import org.springframework.stereotype.Service;
import site.soulware.cocina360.subscriptions.application.subscription.BillingGateway;
import site.soulware.cocina360.subscriptions.domain.model.exception.BillingActivationFailedException;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;

import java.time.Instant;
import java.util.UUID;

@Service
class StripeBillingGateway implements BillingGateway {

    private final StripePriceResolver priceResolver;

    StripeBillingGateway(StripePriceResolver priceResolver) {
        this.priceResolver = priceResolver;
    }

    @Override
    public BillingIds activate(UUID organizationId, String paymentMethodId, SubscriptionPlan plan) {
        try {
            Customer customer = Customer.create(CustomerCreateParams.builder()
                    .putMetadata("organizationId", organizationId.toString())
                    .build());

            PaymentMethod attachedPm = PaymentMethod.retrieve(paymentMethodId).attach(
                    PaymentMethodAttachParams.builder().setCustomer(customer.getId()).build());

            customer.update(CustomerUpdateParams.builder()
                    .setInvoiceSettings(CustomerUpdateParams.InvoiceSettings.builder()
                            .setDefaultPaymentMethod(attachedPm.getId())
                            .build())
                    .build());

            com.stripe.model.Subscription stripeSubscription = com.stripe.model.Subscription.create(
                    SubscriptionCreateParams.builder()
                            .setCustomer(customer.getId())
                            .addItem(SubscriptionCreateParams.Item.builder()
                                    .setPrice(this.priceResolver.priceIdFor(plan))
                                    .build())
                            .build());

            return new BillingIds(customer.getId(), stripeSubscription.getId());
        } catch (StripeException e) {
            throw new BillingActivationFailedException(e.getMessage());
        }
    }

    @Override
    public void updatePlan(String stripeSubscriptionId, SubscriptionPlan newPlan) {
        try {
            com.stripe.model.Subscription subscription =
                    com.stripe.model.Subscription.retrieve(stripeSubscriptionId);
            String itemId = subscription.getItems().getData().getFirst().getId();

            subscription.update(SubscriptionUpdateParams.builder()
                    .addItem(SubscriptionUpdateParams.Item.builder()
                            .setId(itemId)
                            .setPrice(this.priceResolver.priceIdFor(newPlan))
                            .build())
                    .setCancelAtPeriodEnd(false)
                    .build());
        } catch (StripeException e) {
            throw new BillingActivationFailedException(e.getMessage());
        }
    }

    @Override
    public void scheduleDowngrade(String stripeSubscriptionId) {
        try {
            com.stripe.model.Subscription.retrieve(stripeSubscriptionId).update(
                    SubscriptionUpdateParams.builder().setCancelAtPeriodEnd(true).build());
        } catch (StripeException e) {
            throw new BillingActivationFailedException(e.getMessage());
        }
    }

    @Override
    public void resume(String stripeSubscriptionId) {
        try {
            com.stripe.model.Subscription.retrieve(stripeSubscriptionId).update(
                    SubscriptionUpdateParams.builder().setCancelAtPeriodEnd(false).build());
        } catch (StripeException e) {
            throw new BillingActivationFailedException(e.getMessage());
        }
    }

    @Override
    public BillingSchedule fetchSchedule(String stripeSubscriptionId) {
        try {
            com.stripe.model.Subscription subscription =
                    com.stripe.model.Subscription.retrieve(stripeSubscriptionId);
            Instant periodEnd = subscription.getCurrentPeriodEnd() == null
                    ? null
                    : Instant.ofEpochSecond(subscription.getCurrentPeriodEnd());
            return new BillingSchedule(periodEnd, Boolean.TRUE.equals(subscription.getCancelAtPeriodEnd()));
        } catch (StripeException e) {
            throw new BillingActivationFailedException(e.getMessage());
        }
    }
}
