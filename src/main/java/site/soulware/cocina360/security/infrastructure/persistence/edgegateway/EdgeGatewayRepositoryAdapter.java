package site.soulware.cocina360.security.infrastructure.persistence.edgegateway;

import org.springframework.stereotype.Repository;
import site.soulware.cocina360.security.domain.model.aggregate.EdgeGateway;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeGatewayId;
import site.soulware.cocina360.security.domain.repository.EdgeGatewayRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.Optional;

@Repository
public class EdgeGatewayRepositoryAdapter implements EdgeGatewayRepository {

    private final EdgeGatewayJpaRepository jpaRepository;

    public EdgeGatewayRepositoryAdapter(EdgeGatewayJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public EdgeGateway save(EdgeGateway aggregate) {
        EdgeGatewayJpaEntity saved = this.jpaRepository.save(this.toJpaEntity(aggregate));
        return this.toDomain(saved);
    }

    @Override
    public Optional<EdgeGateway> findById(EdgeGatewayId id) {
        return this.jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<EdgeGateway> findByOrganizationId(OrganizationId organizationId) {
        return this.jpaRepository.findByOrganizationId(organizationId.value()).map(this::toDomain);
    }

    @Override
    public boolean existsByOrganizationId(OrganizationId organizationId) {
        return this.jpaRepository.existsByOrganizationId(organizationId.value());
    }

    @Override
    public void delete(EdgeGateway aggregate) {
        this.jpaRepository.deleteById(aggregate.getId().value());
    }

    private EdgeGatewayJpaEntity toJpaEntity(EdgeGateway gateway) {
        return new EdgeGatewayJpaEntity(
                gateway.getId().value(),
                gateway.getOrganizationId().value(),
                gateway.getName(),
                gateway.getStatus(),
                gateway.getApiKey().value(),
                gateway.getCreatedAt(),
                gateway.getCreatedBy().value(),
                gateway.getUpdatedAt(),
                gateway.getUpdatedBy().value()
        );
    }

    private EdgeGateway toDomain(EdgeGatewayJpaEntity entity) {
        return EdgeGateway.rehydrate(
                EdgeGatewayId.of(entity.getId()),
                OrganizationId.of(entity.getOrganizationId()),
                entity.getName(),
                entity.getStatus(),
                ApiKey.of(entity.getApiKey()),
                entity.getCreatedAt(),
                ProfileId.of(entity.getCreatedBy()),
                entity.getUpdatedAt(),
                ProfileId.of(entity.getUpdatedBy())
        );
    }
}
