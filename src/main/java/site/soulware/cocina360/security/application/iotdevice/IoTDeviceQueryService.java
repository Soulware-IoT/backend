package site.soulware.cocina360.security.application.iotdevice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.security.domain.model.exception.IoTDeviceNotFoundException;
import site.soulware.cocina360.security.domain.model.query.GetIoTDeviceQuery;
import site.soulware.cocina360.security.domain.model.query.ListDevicesByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceId;
import site.soulware.cocina360.security.domain.repository.IoTDeviceRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class IoTDeviceQueryService {

    private final IoTDeviceRepository deviceRepository;

    public IoTDeviceQueryService(IoTDeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public IoTDeviceResult handle(GetIoTDeviceQuery query) {
        return this.deviceRepository.findById(IoTDeviceId.of(query.deviceId()))
                .map(IoTDeviceResult::from)
                .orElseThrow(() -> IoTDeviceNotFoundException.byId(query.deviceId()));
    }

    public List<IoTDeviceResult> handle(ListDevicesByOrganizationQuery query) {
        return this.deviceRepository.findByOrganizationId(OrganizationId.of(query.organizationId())).stream()
                .map(IoTDeviceResult::from)
                .toList();
    }
}
