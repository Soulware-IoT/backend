package site.soulware.cocina360.security.application.device;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.security.domain.model.aggregate.Device;
import site.soulware.cocina360.security.domain.model.command.RegisterDeviceCommand;
import site.soulware.cocina360.security.domain.model.exception.DeviceCodeAlreadyRegisteredException;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;
import site.soulware.cocina360.security.domain.repository.DeviceRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

@Service
@Transactional
public class DeviceCommandService {

    private final DeviceRepository deviceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DeviceCommandService(DeviceRepository deviceRepository, ApplicationEventPublisher eventPublisher) {
        this.deviceRepository = deviceRepository;
        this.eventPublisher = eventPublisher;
    }

    public DeviceId handle(RegisterDeviceCommand command) {
        DeviceCode code = DeviceCode.of(command.code());
        if (this.deviceRepository.existsByCode(code)) {
            throw new DeviceCodeAlreadyRegisteredException(code.value());
        }

        DeviceId id = DeviceId.generate();
        SafetyThresholds thresholds = command.thresholds() != null
                ? command.thresholds()
                : SafetyThresholds.defaults();

        Device device = Device.register(
                id, OrganizationId.of(command.organizationId()), code, command.name(), thresholds);

        this.deviceRepository.save(device);
        device.pullDomainEvents().forEach(this.eventPublisher::publishEvent);

        return id;
    }
}
