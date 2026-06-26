package site.soulware.cocina360.internalcontrol.domain.repository;

import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlRegistry;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlRegistryId;
import site.soulware.cocina360.shared.domain.repository.DomainRepository;

import java.util.List;

public interface ControlRegistryRepository extends DomainRepository<ControlRegistry, ControlRegistryId> {

    List<ControlRegistry> findAllByFormatId(ControlFormatId formatId);
}
