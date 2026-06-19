package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat;

import org.springframework.stereotype.Repository;
import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlFormat;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlProcessId;
import site.soulware.cocina360.internalcontrol.domain.repository.ControlFormatRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class ControlFormatRepositoryAdapter implements ControlFormatRepository {

    private final ControlFormatJpaRepository jpaRepository;

    public ControlFormatRepositoryAdapter(ControlFormatJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ControlFormat save(ControlFormat aggregate) {
        ControlFormatJpaEntity saved = this.jpaRepository.save(this.toJpaEntity(aggregate));
        return this.toDomain(saved);
    }

    @Override
    public Optional<ControlFormat> findById(ControlFormatId id) {
        return this.jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<ControlFormat> findAllByProcessId(ControlProcessId processId) {
        return this.jpaRepository.findAllByProcessId(processId.value())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void delete(ControlFormat aggregate) {
        this.jpaRepository.deleteById(aggregate.getId().value());
    }

    private ControlFormatJpaEntity toJpaEntity(ControlFormat format) {
        // Field persistence is deferred to the add-field feature; a format is created empty.
        return new ControlFormatJpaEntity(
                format.getId().value(),
                format.getProcessId().value(),
                format.getName(),
                format.getStatus(),
                format.getCreatedAt(),
                format.getUpdatedAt()
        );
    }

    private ControlFormat toDomain(ControlFormatJpaEntity entity) {
        return ControlFormat.rehydrate(
                ControlFormatId.of(entity.getId()),
                ControlProcessId.of(entity.getProcessId()),
                entity.getName(),
                entity.getStatus(),
                List.of(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
