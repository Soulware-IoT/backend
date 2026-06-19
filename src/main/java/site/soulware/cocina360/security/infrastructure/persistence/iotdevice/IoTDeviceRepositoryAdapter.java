package site.soulware.cocina360.security.infrastructure.persistence.iotdevice;

import org.springframework.stereotype.Repository;

import site.soulware.cocina360.security.domain.model.aggregate.IoTDevice;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;
import site.soulware.cocina360.security.domain.repository.IoTDeviceRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class IoTDeviceRepositoryAdapter implements IoTDeviceRepository {

    private final IoTDeviceJpaRepository jpaRepository;

    public IoTDeviceRepositoryAdapter(IoTDeviceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public IoTDevice save(IoTDevice aggregate) {
        IoTDeviceJpaEntity saved = this.jpaRepository.save(this.toJpaEntity(aggregate));
        return this.toDomain(saved);
    }

    @Override
    public Optional<IoTDevice> findById(IoTDeviceId id) {
        return this.jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<IoTDevice> findByCode(IoTDeviceCode code) {
        return this.jpaRepository.findByCode(code.value()).map(this::toDomain);
    }

    @Override
    public List<IoTDevice> findByOrganizationId(OrganizationId organizationId) {
        return this.jpaRepository.findByOrganizationId(organizationId.value()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCode(IoTDeviceCode code) {
        return this.jpaRepository.existsByCode(code.value());
    }

    @Override
    public void delete(IoTDevice aggregate) {
        this.jpaRepository.deleteById(aggregate.getId().value());
    }

    private IoTDeviceJpaEntity toJpaEntity(IoTDevice device) {
        SafetyThresholds thresholds = device.getThresholds();
        return new IoTDeviceJpaEntity(
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

    private IoTDevice toDomain(IoTDeviceJpaEntity entity) {
        return IoTDevice.rehydrate(
                IoTDeviceId.of(entity.getId()),
                IoTDeviceCode.of(entity.getCode()),
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
