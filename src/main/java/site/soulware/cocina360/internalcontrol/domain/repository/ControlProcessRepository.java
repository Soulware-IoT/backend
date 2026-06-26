package site.soulware.cocina360.internalcontrol.domain.repository;

import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlProcess;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlProcessId;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.repository.DomainRepository;

import java.util.List;

public interface ControlProcessRepository extends DomainRepository<ControlProcess, ControlProcessId> {

    List<ControlProcess> findAllByOrganizationId(OrganizationId organizationId);

    boolean existsById(ControlProcessId id);
}
