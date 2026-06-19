package site.soulware.cocina360.security.infrastructure.persistence.edgedevice;

import org.springframework.stereotype.Repository;
import site.soulware.cocina360.security.domain.model.aggregate.EdgeDevice;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceId;
import site.soulware.cocina360.security.domain.repository.EdgeDeviceRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.Optional;
import java.util.UUID;

@Repository
public class EdgeDeviceRepositoryAdapter implements EdgeDeviceRepository {

    private final EdgeDeviceJpaRepository jpaRepository;

    public EdgeDeviceRepositoryAdapter(EdgeDeviceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public EdgeDevice save(EdgeDevice aggregate) {
        EdgeDeviceJpaEntity saved = this.jpaRepository.save(this.toJpaEntity(aggregate));
        return this.toDomain(saved);
    }

    @Override
    public Optional<EdgeDevice> findById(EdgeDeviceId id) {
        return this.jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<EdgeDevice> findByOrganizationId(OrganizationId organizationId) {
        return this.jpaRepository.findByOrganizationId(organizationId.value()).map(this::toDomain);
    }

    @Override
    public Optional<EdgeDevice> findByApiKey(ApiKey apiKey) {
        return this.jpaRepository.findByApiKey(apiKey.value()).map(this::toDomain);
    }

    @Override
    public Optional<EdgeDevice> findByCode(EdgeDeviceCode code) {
        return this.jpaRepository.findByCode(code.value()).map(this::toDomain);
    }

    @Override
    public boolean existsByOrganizationId(OrganizationId organizationId) {
        return this.jpaRepository.existsByOrganizationId(organizationId.value());
    }

    @Override
    public boolean existsByCode(EdgeDeviceCode code) {
        return this.jpaRepository.existsByCode(code.value());
    }

    @Override
    public void delete(EdgeDevice aggregate) {
        this.jpaRepository.deleteById(aggregate.getId().value());
    }

    private EdgeDeviceJpaEntity toJpaEntity(EdgeDevice edgeDevice) {
        return new EdgeDeviceJpaEntity(
                edgeDevice.getId().value(),
                value(edgeDevice.getOrganizationId()),
                edgeDevice.getCode().value(),
                edgeDevice.getName(),
                edgeDevice.getStatus(),
                edgeDevice.getApiKey().value(),
                edgeDevice.getCreatedAt(),
                value(edgeDevice.getCreatedBy()),
                edgeDevice.getUpdatedAt(),
                value(edgeDevice.getUpdatedBy())
        );
    }

    private EdgeDevice toDomain(EdgeDeviceJpaEntity entity) {
        return EdgeDevice.rehydrate(
                EdgeDeviceId.of(entity.getId()),
                EdgeDeviceCode.of(entity.getCode()),
                ApiKey.of(entity.getApiKey()),
                entity.getOrganizationId() == null ? null : OrganizationId.of(entity.getOrganizationId()),
                entity.getName(),
                entity.getStatus(),
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
