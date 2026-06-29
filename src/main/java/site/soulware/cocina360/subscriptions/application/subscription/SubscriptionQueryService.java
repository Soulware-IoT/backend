package site.soulware.cocina360.subscriptions.application.subscription;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.subscriptions.domain.model.exception.SubscriptionNotFoundException;
import site.soulware.cocina360.subscriptions.domain.model.query.GetSubscriptionByOrganizationQuery;
import site.soulware.cocina360.subscriptions.domain.repository.SubscriptionRepository;

@Service
@Transactional(readOnly = true)
public class SubscriptionQueryService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionQueryService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public SubscriptionResult handle(GetSubscriptionByOrganizationQuery query) {
        return this.subscriptionRepository.findByOrganizationId(OrganizationId.of(query.organizationId()))
                .map(SubscriptionResult::from)
                .orElseThrow(() -> SubscriptionNotFoundException.byOrganizationId(query.organizationId()));
    }
}
