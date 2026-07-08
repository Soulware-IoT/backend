package site.soulware.cocina360.subscriptions.application.subscription;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.subscriptions.domain.model.aggregate.Subscription;
import site.soulware.cocina360.subscriptions.domain.model.exception.SubscriptionNotFoundException;
import site.soulware.cocina360.subscriptions.domain.model.query.GetSubscriptionByOrganizationQuery;
import site.soulware.cocina360.subscriptions.domain.model.query.GetSubscriptionInvoicesQuery;
import site.soulware.cocina360.subscriptions.domain.repository.SubscriptionRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SubscriptionQueryService {

    private final SubscriptionRepository subscriptionRepository;
    private final BillingGateway billingGateway;

    public SubscriptionQueryService(
        SubscriptionRepository subscriptionRepository,
        BillingGateway billingGateway
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.billingGateway = billingGateway;
    }

    /** Plain read — no Stripe call. Used by internal consumers (e.g. device-quota checks). */
    public SubscriptionResult handle(GetSubscriptionByOrganizationQuery query) {
        return SubscriptionResult.from(this.find(query));
    }

    /** Billing-enriched read — fetches the live period end and pending-cancellation flag from Stripe. */
    public SubscriptionResult handleWithBilling(GetSubscriptionByOrganizationQuery query) {
        Subscription subscription = this.find(query);
        BillingGateway.BillingSchedule schedule = subscription.getStripeSubscriptionId() == null
                ? BillingGateway.BillingSchedule.none()
                : this.billingGateway.fetchSchedule(subscription.getStripeSubscriptionId());
        return SubscriptionResult.from(subscription, schedule);
    }

    /** Reads the org's Stripe invoice history. Empty for an org that never subscribed (no Stripe customer). */
    public List<BillingGateway.InvoiceView> listInvoices(GetSubscriptionInvoicesQuery query) {
        Subscription subscription = this.subscriptionRepository
                .findByOrganizationId(OrganizationId.of(query.organizationId()))
                .orElseThrow(() -> SubscriptionNotFoundException.byOrganizationId(query.organizationId()));

        if (subscription.getStripeCustomerId() == null) return List.of();
        return this.billingGateway.listInvoices(subscription.getStripeCustomerId());
    }

    private Subscription find(GetSubscriptionByOrganizationQuery query) {
        return this.subscriptionRepository.findByOrganizationId(OrganizationId.of(query.organizationId()))
                .orElseThrow(() -> SubscriptionNotFoundException.byOrganizationId(query.organizationId()));
    }
}
