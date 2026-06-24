package site.soulware.cocina360.security.domain.repository;

import site.soulware.cocina360.security.domain.model.aggregate.IoTDevice;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceId;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.repository.DomainRepository;

import java.util.List;
import java.util.Optional;

public interface IoTDeviceRepository extends DomainRepository<IoTDevice, IoTDeviceId> {

    Optional<IoTDevice> findByCode(IoTDeviceCode code);

    List<IoTDevice> findByOrganizationId(OrganizationId organizationId);

    boolean existsByCode(IoTDeviceCode code);
}
