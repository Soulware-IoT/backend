package site.soulware.cocina360.security.application.iotdevice;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.security.domain.model.aggregate.IoTDevice;
import site.soulware.cocina360.security.domain.model.command.ClaimDeviceCommand;
import site.soulware.cocina360.security.domain.model.command.UpdateIoTDeviceCommand;
import site.soulware.cocina360.security.domain.model.exception.IoTDeviceNotFoundException;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;
import site.soulware.cocina360.security.domain.repository.IoTDeviceRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.UUID;

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

    /**
     * Partial update of a claimed device: applies whichever of name, thresholds, and
     * activation status are present, each audited to the requester.
     *
     * @throws IoTDeviceNotFoundException if no device exists with that id.
     */
    public void handle(UpdateIoTDeviceCommand command) {
        IoTDevice device = this.require(command.deviceId());
        ProfileId requesterId = ProfileId.of(command.requesterId());

        if (command.name() != null) {
            device.rename(command.name(), requesterId);
        }
        if (command.thresholds() != null) {
            device.updateThresholds(command.thresholds(), requesterId);
        }
        if (command.activate() != null) {
            if (command.activate()) {
                device.activate(requesterId);
            } else {
                device.deactivate(requesterId);
            }
        }

        this.persist(device);
    }

    private IoTDevice require(UUID deviceId) {
        return this.deviceRepository.findById(IoTDeviceId.of(deviceId))
                .orElseThrow(() -> IoTDeviceNotFoundException.byId(deviceId));
    }

    private void persist(IoTDevice device) {
        this.deviceRepository.save(device);
        device.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    private IoTDeviceCode uniqueCode() {
        IoTDeviceCode code = IoTDeviceCode.generate();
        while (this.deviceRepository.existsByCode(code)) {
            code = IoTDeviceCode.generate();
        }
        return code;
    }
}
