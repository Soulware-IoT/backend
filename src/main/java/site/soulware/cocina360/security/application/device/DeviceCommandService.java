package site.soulware.cocina360.security.application.device;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.security.domain.model.aggregate.Device;
import site.soulware.cocina360.security.domain.model.command.ClaimDeviceCommand;
import site.soulware.cocina360.security.domain.model.exception.DeviceNotFoundException;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;
import site.soulware.cocina360.security.domain.repository.DeviceRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

@Service
@Transactional
public class DeviceCommandService {

    private final DeviceRepository deviceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DeviceCommandService(DeviceRepository deviceRepository, ApplicationEventPublisher eventPublisher) {
        this.deviceRepository = deviceRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Factory step: mint a new unassigned device with a generated, unique code + apiKey.
     * Returns the credentials so they can be burned into the firmware.
     */
    public ProvisionedDeviceResult provision() {
        DeviceCode code = this.uniqueCode();
        Device device = Device.provision(DeviceId.generate(), code, ApiKey.generate());

        this.deviceRepository.save(device);
        device.pullDomainEvents().forEach(this.eventPublisher::publishEvent);

        return ProvisionedDeviceResult.from(device);
    }

    /**
     * Claim a previously provisioned device (by code) into an organization.
     *
     * @throws DeviceNotFoundException if no device exists with that code.
     */
    public DeviceId handle(ClaimDeviceCommand command) {
        DeviceCode code = DeviceCode.of(command.code());
        Device device = this.deviceRepository.findByCode(code)
                .orElseThrow(() -> DeviceNotFoundException.byCode(code.value()));

        SafetyThresholds thresholds = command.thresholds() != null
                ? command.thresholds()
                : SafetyThresholds.defaults();

        device.claim(OrganizationId.of(command.organizationId()), command.name(), thresholds,
                ProfileId.of(command.requesterId()));

        this.deviceRepository.save(device);
        device.pullDomainEvents().forEach(this.eventPublisher::publishEvent);

        return device.getId();
    }

    private DeviceCode uniqueCode() {
        DeviceCode code = DeviceCode.generate();
        while (this.deviceRepository.existsByCode(code)) {
            code = DeviceCode.generate();
        }
        return code;
    }
}
