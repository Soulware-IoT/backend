package site.soulware.cocina360.organizations.infrastructure.persistence.organization;

import org.springframework.stereotype.Repository;
import site.soulware.cocina360.organizations.domain.model.aggregate.Organization;
import site.soulware.cocina360.organizations.domain.repository.OrganizationRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.Optional;

@Repository
public class OrganizationRepositoryAdapter implements OrganizationRepository {

    private final OrganizationJpaRepository jpaRepository;

    public OrganizationRepositoryAdapter(OrganizationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Organization save(Organization aggregate) {
        var a = this.toJpaEntity(aggregate);
        OrganizationJpaEntity saved = this.jpaRepository.save(a);
        return this.toDomain(saved);
    }

    @Override
    public Optional<Organization> findById(OrganizationId id) {
        return this.jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public void delete(Organization aggregate) {
        this.jpaRepository.deleteById(aggregate.getId().value());
    }

    private OrganizationJpaEntity toJpaEntity(Organization org) {
        return new OrganizationJpaEntity(
                org.getId().value(),
                org.getCreatedBy().value(),
                org.getUpdatedBy().value(),
                org.getOwnedBy().value(),
                org.getName(),
                org.getImageUrl(),
                org.getAddressLineOne(),
                org.getAddressLineTwo(),
                org.getAddressReference(),
                org.getCreatedAt(),
                org.getUpdatedAt()
        );
    }

    private Organization toDomain(OrganizationJpaEntity entity) {
        return Organization.rehydrate(
                OrganizationId.of(entity.getId()),
                entity.getName(),
                entity.getImageUrl(),
                entity.getAddressLineOne(),
                entity.getAddressLineTwo(),
                entity.getAddressReference(),
                entity.getCreatedAt(),
                ProfileId.of(entity.getCreatedBy()),
                entity.getUpdatedAt(),
                ProfileId.of(entity.getUpdatedBy()),
                ProfileId.of(entity.getOwnedBy())
        );
    }
}
