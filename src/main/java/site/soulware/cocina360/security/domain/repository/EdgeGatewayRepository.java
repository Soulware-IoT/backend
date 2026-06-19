package site.soulware.cocina360.security.domain.repository;

import site.soulware.cocina360.security.domain.model.aggregate.EdgeGateway;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeGatewayId;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.repository.DomainRepository;

import java.util.Optional;

public interface EdgeGatewayRepository extends DomainRepository<EdgeGateway, EdgeGatewayId> {

    Optional<EdgeGateway> findByOrganizationId(OrganizationId organizationId);

    boolean existsByOrganizationId(OrganizationId organizationId);
}
