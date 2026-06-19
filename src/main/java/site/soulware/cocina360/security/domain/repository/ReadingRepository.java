package site.soulware.cocina360.security.domain.repository;

import site.soulware.cocina360.security.domain.model.aggregate.Reading;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.ReadingId;
import site.soulware.cocina360.shared.domain.repository.DomainRepository;

import java.util.List;

public interface ReadingRepository extends DomainRepository<Reading, ReadingId> {

    List<Reading> findByDeviceId(DeviceId deviceId);
}
