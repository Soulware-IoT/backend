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
import java.util.Set;
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

        // Synchronize the persisted field rows to match the aggregate's field set by id: mutate the
        // rows that remain (Hibernate dirty-checks → UPDATE), insert the new ones, delete the rows
        // no longer present. Reconciling against the managed rows (instead of delete-all + reinsert
        // with the same ids) avoids a merge issuing an UPDATE on a just-deleted row.
        UUID formatId = aggregate.getId().value();
        Map<UUID, ControlFormatFieldJpaEntity> existing = this.fieldJpaRepository.findAllByFormatId(formatId)
                .stream()
                .collect(Collectors.toMap(ControlFormatFieldJpaEntity::getId, e -> e));
        Set<UUID> desiredIds = aggregate.getFields().stream()
                .map(field -> field.getId().value())
                .collect(Collectors.toSet());

        // Delete removed rows first and flush, so a key freed by a deletion is available before a
        // new field re-uses it (slugged keys can collide with a just-removed field's key).
        existing.values().stream()
                .filter(row -> !desiredIds.contains(row.getId()))
                .forEach(this.fieldJpaRepository::delete);
        this.fieldJpaRepository.flush();

        for (FormatField field : aggregate.getFields()) {
            ControlFormatFieldJpaEntity row = existing.get(field.getId().value());
            if (row == null) {
                this.fieldJpaRepository.save(this.toFieldEntity(formatId, field));
            } else {
                row.update(
                        field.getLabel(),
                        field.getType(),
                        field.isRequired(),
                        field.getDisplayOrder(),
                        this.validationRulesJsonMapper.toJson(field.getValidationRules())
                );
            }
        }

        return this.toDomain(saved, this.fieldJpaRepository.findAllByFormatId(formatId));
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
