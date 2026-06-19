package site.soulware.cocina360.security.application.device;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.security.domain.model.exception.DeviceNotFoundException;
import site.soulware.cocina360.security.domain.model.query.GetDeviceQuery;
import site.soulware.cocina360.security.domain.model.query.ListDevicesByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceId;
import site.soulware.cocina360.security.domain.repository.DeviceRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DeviceQueryService {

    private final DeviceRepository deviceRepository;

    public DeviceQueryService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public DeviceResult handle(GetDeviceQuery query) {
        return this.deviceRepository.findById(DeviceId.of(query.deviceId()))
                .map(DeviceResult::from)
                .orElseThrow(() -> DeviceNotFoundException.byId(query.deviceId()));
    }

    public List<DeviceResult> handle(ListDevicesByOrganizationQuery query) {
        return this.deviceRepository.findByOrganizationId(OrganizationId.of(query.organizationId())).stream()
                .map(DeviceResult::from)
                .toList();
    }
}
