package site.soulware.cocina360.subscriptions.application.subscription;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.organizations.interfaces.acl.OrganizationsApi;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;
import site.soulware.cocina360.subscriptions.domain.model.aggregate.Subscription;
import site.soulware.cocina360.subscriptions.domain.model.command.CancelSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.command.ChangeSubscriptionPlanCommand;
import site.soulware.cocina360.subscriptions.domain.model.command.CreateSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.command.ReactivateSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.command.SuspendSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.exception.SubscriptionNotFoundException;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionId;
import site.soulware.cocina360.subscriptions.domain.repository.SubscriptionRepository;

@Service
@Transactional
public class SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final OrganizationsApi organizationsApi;
    private final ApplicationEventPublisher eventPublisher;

    public SubscriptionCommandService(
        SubscriptionRepository subscriptionRepository,
        OrganizationsApi organizationsApi,
        ApplicationEventPublisher eventPublisher
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.organizationsApi = organizationsApi;
        this.eventPublisher = eventPublisher;
    }

    public SubscriptionId handle(CreateSubscriptionCommand command) {
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        ProfileId ownedBy = this.organizationsApi.requireOwnerProfileId(command.organizationId());

        Subscription subscription = Subscription.create(
                SubscriptionId.generate(), organizationId, ownedBy, command.plan());

        this.subscriptionRepository.save(subscription);
        subscription.pullDomainEvents().forEach(this.eventPublisher::publishEvent);

        return subscription.getId();
    }

    public void handle(ChangeSubscriptionPlanCommand command) {
        Subscription subscription = this.findOrThrow(OrganizationId.of(command.organizationId()));
        subscription.changePlan(command.plan());

        this.subscriptionRepository.save(subscription);
        subscription.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    public void handle(SuspendSubscriptionCommand command) {
        Subscription subscription = this.findOrThrow(OrganizationId.of(command.organizationId()));
        subscription.suspend();

        this.subscriptionRepository.save(subscription);
        subscription.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    public void handle(CancelSubscriptionCommand command) {
        Subscription subscription = this.findOrThrow(OrganizationId.of(command.organizationId()));
        subscription.cancel();

        this.subscriptionRepository.save(subscription);
        subscription.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    public void handle(ReactivateSubscriptionCommand command) {
        Subscription subscription = this.findOrThrow(OrganizationId.of(command.organizationId()));
        subscription.reactivate();

        this.subscriptionRepository.save(subscription);
        subscription.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    private Subscription findOrThrow(OrganizationId organizationId) {
        return this.subscriptionRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> SubscriptionNotFoundException.byOrganizationId(organizationId.value()));
    }
}
