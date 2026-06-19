package site.soulware.cocina360.security.application.iotdevice;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.security.domain.model.aggregate.IoTDevice;
import site.soulware.cocina360.security.domain.model.command.ClaimDeviceCommand;
import site.soulware.cocina360.security.domain.model.exception.IoTDeviceNotFoundException;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;
import site.soulware.cocina360.security.domain.repository.IoTDeviceRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

@Service
@Transactional
public class IoTDeviceCommandService {

    private final IoTDeviceRepository deviceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public IoTDeviceCommandService(IoTDeviceRepository deviceRepository, ApplicationEventPublisher eventPublisher) {
        this.deviceRepository = deviceRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Factory step: mint a new unassigned device with a generated, unique code + apiKey.
     * Returns the credentials so they can be burned into the firmware.
     */
    public ProvisionedIoTDeviceResult provision() {
        IoTDeviceCode code = this.uniqueCode();
        IoTDevice device = IoTDevice.provision(IoTDeviceId.generate(), code, ApiKey.generate());

        this.deviceRepository.save(device);
        device.pullDomainEvents().forEach(this.eventPublisher::publishEvent);

        return ProvisionedIoTDeviceResult.from(device);
    }

    /**
     * Claim a previously provisioned device (by code) into an organization.
     *
     * @throws IoTDeviceNotFoundException if no device exists with that code.
     */
    public IoTDeviceId handle(ClaimDeviceCommand command) {
        IoTDeviceCode code = IoTDeviceCode.of(command.code());
        IoTDevice device = this.deviceRepository.findByCode(code)
                .orElseThrow(() -> IoTDeviceNotFoundException.byCode(code.value()));

        SafetyThresholds thresholds = command.thresholds() != null
                ? command.thresholds()
                : SafetyThresholds.defaults();

        device.claim(OrganizationId.of(command.organizationId()), command.name(), thresholds,
                ProfileId.of(command.requesterId()));

        this.deviceRepository.save(device);
        device.pullDomainEvents().forEach(this.eventPublisher::publishEvent);

        return device.getId();
    }

    private IoTDeviceCode uniqueCode() {
        IoTDeviceCode code = IoTDeviceCode.generate();
        while (this.deviceRepository.existsByCode(code)) {
            code = IoTDeviceCode.generate();
        }
        return code;
    }
}
