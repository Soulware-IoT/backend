package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat;

import org.springframework.stereotype.Repository;
import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlFormat;
import site.soulware.cocina360.internalcontrol.domain.model.entity.FormatField;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlProcessId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FormatFieldId;
import site.soulware.cocina360.internalcontrol.domain.repository.ControlFormatRepository;
import site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat.jpa.ControlFormatFieldJpaEntity;
import site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat.jpa.ControlFormatFieldJpaRepository;
import site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat.jpa.ControlFormatJpaEntity;
import site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat.jpa.ControlFormatJpaRepository;
import site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat.jpa.ValidationRulesJsonMapper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ControlFormatRepositoryAdapter implements ControlFormatRepository {

    private final ControlFormatJpaRepository jpaRepository;
    private final ControlFormatFieldJpaRepository fieldJpaRepository;
    private final ValidationRulesJsonMapper validationRulesJsonMapper;

    public ControlFormatRepositoryAdapter(
        ControlFormatJpaRepository jpaRepository,
        ControlFormatFieldJpaRepository fieldJpaRepository,
        ValidationRulesJsonMapper validationRulesJsonMapper
    ) {
        this.jpaRepository = jpaRepository;
        this.fieldJpaRepository = fieldJpaRepository;
        this.validationRulesJsonMapper = validationRulesJsonMapper;
    }

    @Override
    public ControlFormat save(ControlFormat aggregate) {
        ControlFormatJpaEntity saved = this.jpaRepository.save(this.toJpaEntity(aggregate));

        // The aggregate owns its fields: replace the persisted set wholesale so add/update/remove
        // all converge to one path. Flush the delete before re-inserting to honour the
        // unique (format_id, key) constraint when a key is reused.
        UUID formatId = aggregate.getId().value();
        this.fieldJpaRepository.deleteByFormatId(formatId);
        this.fieldJpaRepository.flush();
        List<ControlFormatFieldJpaEntity> fieldEntities = aggregate.getFields().stream()
                .map(field -> this.toFieldEntity(formatId, field))
                .toList();
        this.fieldJpaRepository.saveAll(fieldEntities);

        return this.toDomain(saved, fieldEntities);
    }

    @Override
    public Optional<ControlFormat> findById(ControlFormatId id) {
        return this.jpaRepository.findById(id.value())
                .map(entity -> this.toDomain(entity, this.fieldJpaRepository.findAllByFormatId(entity.getId())));
    }

    @Override
    public List<ControlFormat> findAllByProcessId(ControlProcessId processId) {
        List<ControlFormatJpaEntity> formats = this.jpaRepository.findAllByProcessId(processId.value());
        if (formats.isEmpty()) {
            return List.of();
        }
        List<UUID> formatIds = formats.stream().map(ControlFormatJpaEntity::getId).toList();
        Map<UUID, List<ControlFormatFieldJpaEntity>> fieldsByFormat = this.fieldJpaRepository
                .findAllByFormatIdIn(formatIds)
                .stream()
                .collect(Collectors.groupingBy(ControlFormatFieldJpaEntity::getFormatId));
        return formats.stream()
                .map(entity -> this.toDomain(entity, fieldsByFormat.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    @Override
    public void delete(ControlFormat aggregate) {
        this.jpaRepository.deleteById(aggregate.getId().value());
    }

    private ControlFormatJpaEntity toJpaEntity(ControlFormat format) {
        return new ControlFormatJpaEntity(
                format.getId().value(),
                format.getProcessId().value(),
                format.getName(),
                format.getStatus(),
                format.getCreatedAt(),
                format.getUpdatedAt()
        );
    }

    private ControlFormatFieldJpaEntity toFieldEntity(UUID formatId, FormatField field) {
        return new ControlFormatFieldJpaEntity(
                field.getId().value(),
                formatId,
                field.getKey(),
                field.getLabel(),
                field.getType(),
                field.isRequired(),
                field.getDisplayOrder(),
                this.validationRulesJsonMapper.toJson(field.getValidationRules())
        );
    }

    private ControlFormat toDomain(ControlFormatJpaEntity entity, List<ControlFormatFieldJpaEntity> fieldEntities) {
        List<FormatField> fields = fieldEntities.stream()
                .sorted(Comparator.comparingInt(ControlFormatFieldJpaEntity::getDisplayOrder))
                .map(this::toFieldDomain)
                .toList();
        return ControlFormat.rehydrate(
                ControlFormatId.of(entity.getId()),
                ControlProcessId.of(entity.getProcessId()),
                entity.getName(),
                entity.getStatus(),
                fields,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private FormatField toFieldDomain(ControlFormatFieldJpaEntity entity) {
        return FormatField.rehydrate(
                FormatFieldId.of(entity.getId()),
                entity.getKey(),
                entity.getLabel(),
                entity.getType(),
                entity.isRequired(),
                entity.getDisplayOrder(),
                this.validationRulesJsonMapper.toDomain(entity.getValidationRules())
        );
    }
}
