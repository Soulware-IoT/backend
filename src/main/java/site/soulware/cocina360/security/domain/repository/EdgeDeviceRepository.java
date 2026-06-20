package site.soulware.cocina360.security.domain.repository;

import site.soulware.cocina360.security.domain.model.aggregate.EdgeDevice;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceId;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.repository.DomainRepository;

import java.util.Optional;

public interface EdgeDeviceRepository extends DomainRepository<EdgeDevice, EdgeDeviceId> {

    Optional<EdgeDevice> findByOrganizationId(OrganizationId organizationId);

    Optional<EdgeDevice> findByApiKey(ApiKey apiKey);

    Optional<EdgeDevice> findByCode(EdgeDeviceCode code);

    boolean existsByOrganizationId(OrganizationId organizationId);

    boolean existsByCode(EdgeDeviceCode code);
}
