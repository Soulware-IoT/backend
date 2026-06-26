package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlprocess;

import org.springframework.stereotype.Repository;
import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlProcess;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlProcessId;
import site.soulware.cocina360.internalcontrol.domain.repository.ControlProcessRepository;
import site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlprocess.jpa.ControlProcessJpaEntity;
import site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlprocess.jpa.ControlProcessJpaRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

import java.util.List;
import java.util.Optional;

@Repository
public class ControlProcessRepositoryAdapter implements ControlProcessRepository {

    private final ControlProcessJpaRepository jpaRepository;

    public ControlProcessRepositoryAdapter(ControlProcessJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ControlProcess save(ControlProcess aggregate) {
        ControlProcessJpaEntity saved = this.jpaRepository.save(this.toJpaEntity(aggregate));
        return this.toDomain(saved);
    }

    @Override
    public Optional<ControlProcess> findById(ControlProcessId id) {
        return this.jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<ControlProcess> findAllByOrganizationId(OrganizationId organizationId) {
        return this.jpaRepository.findAllByOrganizationId(organizationId.value())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(ControlProcessId id) {
        return this.jpaRepository.existsById(id.value());
    }

    @Override
    public void delete(ControlProcess aggregate) {
        this.jpaRepository.deleteById(aggregate.getId().value());
    }

    private ControlProcessJpaEntity toJpaEntity(ControlProcess process) {
        return new ControlProcessJpaEntity(
                process.getId().value(),
                process.getOrganizationId().value(),
                process.getName(),
                process.getCreatedAt(),
                process.getUpdatedAt()
        );
    }

    private ControlProcess toDomain(ControlProcessJpaEntity entity) {
        return ControlProcess.rehydrate(
                ControlProcessId.of(entity.getId()),
                OrganizationId.of(entity.getOrganizationId()),
                entity.getName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
