package site.soulware.cocina360.internalcontrol.domain.repository;

import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlFormat;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlProcessId;
import site.soulware.cocina360.shared.domain.repository.DomainRepository;

import java.util.List;

public interface ControlFormatRepository extends DomainRepository<ControlFormat, ControlFormatId> {

    List<ControlFormat> findAllByProcessId(ControlProcessId processId);
}
