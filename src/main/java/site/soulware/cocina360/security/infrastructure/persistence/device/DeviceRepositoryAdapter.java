package site.soulware.cocina360.security.infrastructure.persistence.device;

import org.springframework.stereotype.Repository;
import site.soulware.cocina360.security.domain.model.aggregate.Device;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;
import site.soulware.cocina360.security.domain.repository.DeviceRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DeviceRepositoryAdapter implements DeviceRepository {

    private final DeviceJpaRepository jpaRepository;

    public DeviceRepositoryAdapter(DeviceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Device save(Device aggregate) {
        DeviceJpaEntity saved = this.jpaRepository.save(this.toJpaEntity(aggregate));
        return this.toDomain(saved);
    }

    @Override
    public Optional<Device> findById(DeviceId id) {
        return this.jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Device> findByCode(DeviceCode code) {
        return this.jpaRepository.findByCode(code.value()).map(this::toDomain);
    }

    @Override
    public List<Device> findByOrganizationId(OrganizationId organizationId) {
        return this.jpaRepository.findByOrganizationId(organizationId.value()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCode(DeviceCode code) {
        return this.jpaRepository.existsByCode(code.value());
    }

    @Override
    public void delete(Device aggregate) {
        this.jpaRepository.deleteById(aggregate.getId().value());
    }

    private DeviceJpaEntity toJpaEntity(Device device) {
        SafetyThresholds thresholds = device.getThresholds();
        return new DeviceJpaEntity(
                device.getId().value(),
                value(device.getOrganizationId()),
                device.getCode().value(),
                device.getName(),
                device.getStatus(),
                device.getApiKey().value(),
                thresholds.warnTemperatureC(),
                thresholds.critTemperatureC(),
                thresholds.warnGasPpm(),
                thresholds.critGasPpm(),
                device.getCreatedAt(),
                value(device.getCreatedBy()),
                device.getUpdatedAt(),
                value(device.getUpdatedBy())
        );
    }

    private Device toDomain(DeviceJpaEntity entity) {
        return Device.rehydrate(
                DeviceId.of(entity.getId()),
                DeviceCode.of(entity.getCode()),
                ApiKey.of(entity.getApiKey()),
                entity.getOrganizationId() == null ? null : OrganizationId.of(entity.getOrganizationId()),
                entity.getName(),
                entity.getStatus(),
                new SafetyThresholds(
                        entity.getWarnTemperatureC(),
                        entity.getCritTemperatureC(),
                        entity.getWarnGasPpm(),
                        entity.getCritGasPpm()),
                entity.getCreatedAt(),
                entity.getCreatedBy() == null ? null : ProfileId.of(entity.getCreatedBy()),
                entity.getUpdatedAt(),
                entity.getUpdatedBy() == null ? null : ProfileId.of(entity.getUpdatedBy())
        );
    }

    private static UUID value(OrganizationId id) {
        return id == null ? null : id.value();
    }

    private static UUID value(ProfileId id) {
        return id == null ? null : id.value();
    }
}
