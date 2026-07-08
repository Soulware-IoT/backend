package site.soulware.cocina360.security.application.reading;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import site.soulware.cocina360.security.domain.model.aggregate.IoTDevice;
import site.soulware.cocina360.security.domain.model.aggregate.Reading;
import site.soulware.cocina360.security.domain.model.command.RecordReadingsCommand;
import site.soulware.cocina360.security.domain.model.exception.IoTDeviceNotFoundException;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.ReadingId;
import site.soulware.cocina360.security.domain.repository.IoTDeviceRepository;
import site.soulware.cocina360.security.domain.repository.ReadingRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

@Service
@Transactional
public class ReadingCommandService {

    private final ReadingRepository readingRepository;
    private final IoTDeviceRepository deviceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReadingCommandService(
        ReadingRepository readingRepository,
        IoTDeviceRepository deviceRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.readingRepository = readingRepository;
        this.deviceRepository = deviceRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Record a batch of readings forwarded by an organization's edge. Each entry is
     * matched to one of the organization's devices (by code), persisted to the reading
     * ledger, and any domain events it raised (e.g. {@code CriticalReadingDetected} for a
     * CRITICAL reading) are published.
     *
     * @throws IoTDeviceNotFoundException if an entry's device code is unknown or the device
     *         does not belong to the given organization.
     */
    public void handle(RecordReadingsCommand command) {
        OrganizationId organizationId = OrganizationId.of(command.organizationId());

        for (RecordReadingsCommand.Entry entry : command.readings()) {
            IoTDevice device = this.resolveDevice(entry.deviceCode(), organizationId);

            Reading reading = Reading.record(
                    ReadingId.generate(),
                    device.getOrganizationId(),
                    device.getId(),
                    entry.temperatureC(),
                    entry.gasPpm(),
                    entry.severity(),
                    entry.occurredAt());

            this.readingRepository.save(reading);
            reading.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
        }
    }

    /**
     * Resolve a device by its code, scoped to the calling edge's organization. A device
     * that exists but belongs to another organization is reported as not found — from this
     * organization's perspective it is not part of its fleet.
     */
    private IoTDevice resolveDevice(String code, OrganizationId organizationId) {
        IoTDevice device = this.deviceRepository.findByCode(IoTDeviceCode.of(code))
                .orElseThrow(() -> IoTDeviceNotFoundException.byCode(code));

        if (!organizationId.equals(device.getOrganizationId())) {
            throw IoTDeviceNotFoundException.byCode(code);
        }
        return device;
    }
}
