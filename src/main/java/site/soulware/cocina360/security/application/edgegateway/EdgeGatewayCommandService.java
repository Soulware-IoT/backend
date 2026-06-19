package site.soulware.cocina360.security.application.edgegateway;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.security.domain.model.aggregate.EdgeGateway;
import site.soulware.cocina360.security.domain.model.command.RegisterEdgeGatewayCommand;
import site.soulware.cocina360.security.domain.model.exception.OrganizationAlreadyHasEdgeGatewayException;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeGatewayId;
import site.soulware.cocina360.security.domain.repository.EdgeGatewayRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

@Service
@Transactional
public class EdgeGatewayCommandService {

    private final EdgeGatewayRepository edgeGatewayRepository;
    private final ApplicationEventPublisher eventPublisher;

    public EdgeGatewayCommandService(
        EdgeGatewayRepository edgeGatewayRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.edgeGatewayRepository = edgeGatewayRepository;
        this.eventPublisher = eventPublisher;
    }

    public EdgeGatewayId handle(RegisterEdgeGatewayCommand command) {
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        if (this.edgeGatewayRepository.existsByOrganizationId(organizationId)) {
            throw new OrganizationAlreadyHasEdgeGatewayException(organizationId.value());
        }

        EdgeGatewayId id = EdgeGatewayId.generate();
        EdgeGateway gateway = EdgeGateway.register(id, organizationId, command.name());

        this.edgeGatewayRepository.save(gateway);
        gateway.pullDomainEvents().forEach(this.eventPublisher::publishEvent);

        return id;
    }
}
