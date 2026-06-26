package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlregistry;

import org.springframework.stereotype.Repository;
import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlRegistry;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlRegistryId;
import site.soulware.cocina360.internalcontrol.domain.repository.ControlRegistryRepository;
import site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlregistry.jpa.ControlRegistryJpaEntity;
import site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlregistry.jpa.ControlRegistryJpaRepository;
import site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlregistry.jpa.RegistryDataJsonMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class ControlRegistryRepositoryAdapter implements ControlRegistryRepository {

    private final ControlRegistryJpaRepository jpaRepository;
    private final RegistryDataJsonMapper dataJsonMapper;

    public ControlRegistryRepositoryAdapter(
        ControlRegistryJpaRepository jpaRepository,
        RegistryDataJsonMapper dataJsonMapper
    ) {
        this.jpaRepository = jpaRepository;
        this.dataJsonMapper = dataJsonMapper;
    }

    @Override
    public ControlRegistry save(ControlRegistry aggregate) {
        ControlRegistryJpaEntity saved = this.jpaRepository.save(this.toJpaEntity(aggregate));
        return this.toDomain(saved);
    }

    @Override
    public Optional<ControlRegistry> findById(ControlRegistryId id) {
        return this.jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<ControlRegistry> findAllByFormatId(ControlFormatId formatId) {
        return this.jpaRepository.findAllByFormatId(formatId.value())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void delete(ControlRegistry aggregate) {
        this.jpaRepository.deleteById(aggregate.getId().value());
    }

    private ControlRegistryJpaEntity toJpaEntity(ControlRegistry registry) {
        return new ControlRegistryJpaEntity(
                registry.getId().value(),
                registry.getFormatId().value(),
                this.dataJsonMapper.toJson(registry.getData()),
                registry.getCreatedAt()
        );
    }

    private ControlRegistry toDomain(ControlRegistryJpaEntity entity) {
        return ControlRegistry.rehydrate(
                ControlRegistryId.of(entity.getId()),
                ControlFormatId.of(entity.getFormatId()),
                this.dataJsonMapper.toDomain(entity.getData()),
                entity.getCreatedAt()
        );
    }
}
