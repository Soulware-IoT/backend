package site.soulware.cocina360.security.domain.repository;

import site.soulware.cocina360.security.domain.model.aggregate.Device;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceId;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.repository.DomainRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends DomainRepository<Device, DeviceId> {

    Optional<Device> findByCode(DeviceCode code);

    List<Device> findByOrganizationId(OrganizationId organizationId);

    boolean existsByCode(DeviceCode code);
}
