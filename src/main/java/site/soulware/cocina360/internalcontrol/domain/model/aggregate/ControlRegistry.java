package site.soulware.cocina360.internalcontrol.domain.model.aggregate;

import site.soulware.cocina360.internalcontrol.domain.model.entity.FormatField;
import site.soulware.cocina360.internalcontrol.domain.model.event.ControlRegistryCreated;
import site.soulware.cocina360.internalcontrol.domain.model.exception.FormatNotActiveException;
import site.soulware.cocina360.internalcontrol.domain.model.exception.RegistryValidationException;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatStatus;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlRegistryId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ValidationRules;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ControlRegistry extends AggregateRoot<ControlRegistryId> {

    private final ControlRegistryId id;
    private final ControlFormatId formatId;
    private final Map<String, Object> data;
    private final Instant createdAt;

    private ControlRegistry(
        ControlRegistryId id,
        ControlFormatId formatId,
        Map<String, Object> data,
        Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.formatId = Objects.requireNonNull(formatId, "formatId must not be null");
        this.data = Collections.unmodifiableMap(Objects.requireNonNull(data, "data must not be null"));
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static ControlRegistry create(
        ControlRegistryId id,
        ControlFormat format,
        Map<String, Object> rawValues
    ) {
        if (format.getStatus() != ControlFormatStatus.ACTIVE) {
            throw new FormatNotActiveException(format.getId().value(), format.getStatus());
        }

        List<RegistryValidationException.FieldViolation> violations = new ArrayList<>();

        for (FormatField field : format.getFields()) {
            Object value = rawValues.get(field.getKey());

            if (value == null) {
                if (field.isRequired()) {
                    violations.add(new RegistryValidationException.FieldViolation(
                            field.getKey(), "error.control.registry.field.required"));
                }
                continue;
            }

            violations.addAll(validateValue(field, value));
        }

        if (!violations.isEmpty()) {
            throw new RegistryValidationException(violations);
        }

        Instant now = Instant.now();
        ControlRegistry registry = new ControlRegistry(id, format.getId(), Map.copyOf(rawValues), now);
        registry.registerEvent(new ControlRegistryCreated(id.value(), format.getId().value(), now));
        return registry;
    }

    public static ControlRegistry rehydrate(
        ControlRegistryId id,
        ControlFormatId formatId,
        Map<String, Object> data,
        Instant createdAt
    ) {
        return new ControlRegistry(id, formatId, data, createdAt);
    }

    private static List<RegistryValidationException.FieldViolation> validateValue(FormatField field, Object value) {
        List<RegistryValidationException.FieldViolation> violations = new ArrayList<>();
        String key = field.getKey();

        switch (field.getType()) {
            case TEXT -> {
                if (!(value instanceof String str)) {
                    violations.add(new RegistryValidationException.FieldViolation(key, "error.control.registry.field.type_mismatch"));
                    break;
                }
                if (field.getValidationRules() instanceof ValidationRules.Text rules) {
                    if (rules.minLength() != null && str.length() < rules.minLength())
                        violations.add(new RegistryValidationException.FieldViolation(key, "error.control.registry.field.text.min_length"));
                    if (rules.maxLength() != null && str.length() > rules.maxLength())
                        violations.add(new RegistryValidationException.FieldViolation(key, "error.control.registry.field.text.max_length"));
                    if (rules.pattern() != null && !Pattern.matches(rules.pattern(), str))
                        violations.add(new RegistryValidationException.FieldViolation(key, "error.control.registry.field.text.pattern"));
                }
            }
            case NUMBER -> {
                if (!(value instanceof Number num)) {
                    violations.add(new RegistryValidationException.FieldViolation(key, "error.control.registry.field.type_mismatch"));
                    break;
                }
                if (field.getValidationRules() instanceof ValidationRules.Number rules) {
                    double d = num.doubleValue();
                    if (rules.min() != null && d < rules.min())
                        violations.add(new RegistryValidationException.FieldViolation(key, "error.control.registry.field.number.min"));
                    if (rules.max() != null && d > rules.max())
                        violations.add(new RegistryValidationException.FieldViolation(key, "error.control.registry.field.number.max"));
                }
            }
            case BOOLEAN -> {
                if (!(value instanceof Boolean))
                    violations.add(new RegistryValidationException.FieldViolation(key, "error.control.registry.field.type_mismatch"));
            }
            case DATE -> {
                if (!(value instanceof String str)) {
                    violations.add(new RegistryValidationException.FieldViolation(key, "error.control.registry.field.type_mismatch"));
                    break;
                }
                try {
                    LocalDate.parse(str);
                } catch (DateTimeParseException e) {
                    violations.add(new RegistryValidationException.FieldViolation(key, "error.control.registry.field.date.invalid_format"));
                }
            }
            case SELECT -> {
                if (!(value instanceof String str)) {
                    violations.add(new RegistryValidationException.FieldViolation(key, "error.control.registry.field.type_mismatch"));
                    break;
                }
                if (field.getValidationRules() instanceof ValidationRules.Select rules) {
                    if (!rules.options().contains(str))
                        violations.add(new RegistryValidationException.FieldViolation(key, "error.control.registry.field.select.invalid_option"));
                }
            }
        }

        return violations;
    }

    @Override
    public ControlRegistryId getId() { return this.id; }
    public ControlFormatId getFormatId() { return this.formatId; }
    public Map<String, Object> getData() { return this.data; }
    public Instant getCreatedAt() { return this.createdAt; }
}
