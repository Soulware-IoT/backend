package site.soulware.cocina360.subscriptions.application.subscription;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;
import site.soulware.cocina360.subscriptions.domain.model.aggregate.Subscription;
import site.soulware.cocina360.subscriptions.domain.model.command.ChangeSubscriptionPlanCommand;
import site.soulware.cocina360.subscriptions.domain.model.command.CreateSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.command.DowngradeSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.command.ResumeSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.exception.CannotChangeToFreePlanException;
import site.soulware.cocina360.subscriptions.domain.model.exception.PaymentMethodRequiredException;
import site.soulware.cocina360.subscriptions.domain.model.exception.SubscriptionNotFoundException;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionId;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;
import site.soulware.cocina360.subscriptions.domain.repository.SubscriptionRepository;

@Service
@Transactional
public class SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final BillingGateway billingGateway;

    public SubscriptionCommandService(
        SubscriptionRepository subscriptionRepository,
        ApplicationEventPublisher eventPublisher,
        BillingGateway billingGateway
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.eventPublisher = eventPublisher;
        this.billingGateway = billingGateway;
    }

    public SubscriptionId handle(CreateSubscriptionCommand command) {
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        ProfileId ownedBy = ProfileId.of(command.ownedBy());

        Subscription subscription = Subscription.create(
                SubscriptionId.generate(), organizationId, ownedBy, command.plan());

        this.subscriptionRepository.save(subscription);
        subscription.pullDomainEvents().forEach(this.eventPublisher::publishEvent);

        return subscription.getId();
    }

    /** Upgrade or switch between paid plans. Moving to FREE is a downgrade — use {@link DowngradeSubscriptionCommand}. */
    public void handle(ChangeSubscriptionPlanCommand command) {
        if (!command.plan().isPaid()) throw new CannotChangeToFreePlanException();

        Subscription subscription = this.findOrThrow(OrganizationId.of(command.organizationId()));
        this.syncPaidBilling(subscription, command);
        subscription.changePlan(command.plan());

        this.subscriptionRepository.save(subscription);
        subscription.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    private void syncPaidBilling(Subscription subscription, ChangeSubscriptionPlanCommand command) {
        if (subscription.getStripeSubscriptionId() != null) {
            this.billingGateway.updatePlan(subscription.getStripeSubscriptionId(), command.plan());
        } else {
            if (command.paymentMethodId() == null) throw new PaymentMethodRequiredException();
            BillingGateway.BillingIds ids = this.billingGateway.activate(
                    command.organizationId(), command.paymentMethodId(), command.plan());
            subscription.attachBillingIds(ids.customerId(), ids.subscriptionId());
        }
    }

    /**
     * Schedules the org to drop to FREE at the end of the current paid period. The plan stays paid
     * (and quota unchanged) until then; the actual transition to FREE is driven by Stripe's
     * {@code customer.subscription.deleted} webhook when the period ends. Idempotent: a no-op if the
     * org is already on FREE (nothing billed to cancel).
     */
    public void handle(DowngradeSubscriptionCommand command) {
        Subscription subscription = this.findOrThrow(OrganizationId.of(command.organizationId()));
        if (subscription.getStripeSubscriptionId() == null) return;

        this.billingGateway.scheduleDowngrade(subscription.getStripeSubscriptionId());
    }

    /** Cancels a pending end-of-period downgrade — the subscription keeps renewing. No-op if on FREE. */
    public void handle(ResumeSubscriptionCommand command) {
        Subscription subscription = this.findOrThrow(OrganizationId.of(command.organizationId()));
        if (subscription.getStripeSubscriptionId() == null) return;

        this.billingGateway.resume(subscription.getStripeSubscriptionId());
    }

    /**
     * Driven by a Stripe webhook (subscription deleted) — drops the org back to FREE. Idempotent: a no-op
     * if the subscription is already FREE, or if no subscription references this customer anymore (our own
     * downgrade already cleared the Stripe references and Stripe re-delivered the event).
     */
    public void downgradeToFreeByStripeCustomer(String stripeCustomerId) {
        var maybe = this.subscriptionRepository.findByStripeCustomerId(stripeCustomerId);
        if (maybe.isEmpty()) return;
        Subscription subscription = maybe.get();
        if (subscription.getPlan() == SubscriptionPlan.FREE) return;

        subscription.detachBillingIds();
        subscription.changePlan(SubscriptionPlan.FREE);

        this.subscriptionRepository.save(subscription);
        subscription.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    private Subscription findOrThrow(OrganizationId organizationId) {
        return this.subscriptionRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> SubscriptionNotFoundException.byOrganizationId(organizationId.value()));
    }
}
