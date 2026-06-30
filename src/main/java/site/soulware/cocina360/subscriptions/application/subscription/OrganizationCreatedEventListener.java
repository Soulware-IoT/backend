package site.soulware.cocina360.subscriptions.application.subscription;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import site.soulware.cocina360.organizations.domain.model.event.OrganizationCreated;
import site.soulware.cocina360.subscriptions.domain.model.command.CreateSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.valueobject.SubscriptionPlan;
import site.soulware.cocina360.subscriptions.domain.repository.SubscriptionRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

@Component
class OrganizationCreatedEventListener {

    private final SubscriptionCommandService commandService;
    private final SubscriptionRepository subscriptionRepository;

    OrganizationCreatedEventListener(
        SubscriptionCommandService commandService,
        SubscriptionRepository subscriptionRepository
    ) {
        this.commandService = commandService;
        this.subscriptionRepository = subscriptionRepository;
    }

    @ApplicationModuleListener
    void on(OrganizationCreated event) {
        if (this.subscriptionRepository.findByOrganizationId(OrganizationId.of(event.organizationId())).isPresent()) {
            return;
        }
        this.commandService.handle(new CreateSubscriptionCommand(
                event.organizationId(),
                event.createdBy(),
                SubscriptionPlan.FREE
        ));
    }
}
