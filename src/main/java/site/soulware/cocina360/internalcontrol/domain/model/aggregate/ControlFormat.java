package site.soulware.cocina360.internalcontrol.domain.model.aggregate;

import site.soulware.cocina360.internalcontrol.domain.model.event.ControlFormatActivated;
import site.soulware.cocina360.internalcontrol.domain.model.event.ControlFormatCeased;
import site.soulware.cocina360.internalcontrol.domain.model.event.ControlFormatCreated;
import site.soulware.cocina360.internalcontrol.domain.model.event.ControlFormatResumed;
import site.soulware.cocina360.internalcontrol.domain.model.event.ControlFormatSuspended;
import site.soulware.cocina360.internalcontrol.domain.model.entity.FormatField;
import site.soulware.cocina360.internalcontrol.domain.model.exception.CannotActivateFormatException;
import site.soulware.cocina360.internalcontrol.domain.model.exception.CannotCeaseFormatException;
import site.soulware.cocina360.internalcontrol.domain.model.exception.CannotResumeFormatException;
import site.soulware.cocina360.internalcontrol.domain.model.exception.CannotSuspendFormatException;
import site.soulware.cocina360.internalcontrol.domain.model.exception.FormatFieldNotFoundException;
import site.soulware.cocina360.internalcontrol.domain.model.exception.FormatNotEditableException;
import site.soulware.cocina360.internalcontrol.domain.model.exception.InvalidFormatTransitionException;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatStatus;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlProcessId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FieldType;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FormatFieldDraft;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FormatFieldId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.NumberKind;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ValidationRules;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;

import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ControlFormat extends AggregateRoot<ControlFormatId> {

    private final ControlFormatId id;
    private final ControlProcessId processId;
    private String name;
    private ControlFormatStatus status;
    private final List<FormatField> fields;
    private final Instant createdAt;
    private Instant updatedAt;

    private ControlFormat(
        ControlFormatId id,
        ControlProcessId processId,
        String name,
        ControlFormatStatus status,
        List<FormatField> fields,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.processId = Objects.requireNonNull(processId, "processId must not be null");
        this.name = requireName(name);
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.fields = new ArrayList<>(Objects.requireNonNull(fields, "fields must not be null"));
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static ControlFormat create(ControlFormatId id, ControlProcessId processId, String name) {
        Instant now = Instant.now();
        ControlFormat format = new ControlFormat(id, processId, name, ControlFormatStatus.DRAFT,
                List.of(), now, now);

        format.registerEvent(new ControlFormatCreated(id.value(), processId.value(), name, now));
        return format;
    }

    /**
     * Creates a new (DRAFT) format pre-populated with a starter template modelled on the most
     * common restaurant control: checking the quality of incoming supplies. Built on top of
     * {@link #create} so the same creation event is emitted, then the sample fields are added
     * through the regular {@link #addField} path. The fields are immediately editable and
     * removable, so they double as a worked example that lets the user discover customization
     * quickly rather than facing an empty format. The record's timestamp is captured by the
     * registry's {@code createdAt}, so no date field is seeded.
     */
    public static ControlFormat createWithSampleFields(ControlFormatId id, ControlProcessId processId, String name) {
        ControlFormat format = create(id, processId, name);
        format.addField(FormatField.create(FormatFieldId.generate(), "insumo", "Insumo / Producto",
                FieldType.TEXT, true, 0, new ValidationRules.Text(null, 120, null)));
        format.addField(FormatField.create(FormatFieldId.generate(), "temperatura_recepcion", "Temperatura de recepción (°C)",
                FieldType.NUMBER, true, 1, new ValidationRules.Number(NumberKind.DECIMAL, -20.0, 90.0)));
        format.addField(FormatField.create(FormatFieldId.generate(), "evaluacion", "Evaluación",
                FieldType.SELECT, true, 2, new ValidationRules.Select(List.of("aceptado", "rechazado"))));
        format.addField(FormatField.create(FormatFieldId.generate(), "observaciones", "Observaciones",
                FieldType.TEXT, false, 3, new ValidationRules.Text(null, 500, null)));
        return format;
    }

    public static ControlFormat rehydrate(
        ControlFormatId id,
        ControlProcessId processId,
        String name,
        ControlFormatStatus status,
        List<FormatField> fields,
        Instant createdAt,
        Instant updatedAt
    ) {
        return new ControlFormat(id, processId, name, status, fields, createdAt, updatedAt);
    }

    public void activate() {
        try {
            this.requireSource(ControlFormatStatus.DRAFT);
            this.transitionTo(ControlFormatStatus.ACTIVE);
        } catch (InvalidFormatTransitionException e) {
            throw new CannotActivateFormatException(this.status);
        }
        this.registerEvent(new ControlFormatActivated(this.id.value(), this.updatedAt));
    }

    public void suspend() {
        try {
            this.transitionTo(ControlFormatStatus.SUSPENDED);
        } catch (InvalidFormatTransitionException e) {
            throw new CannotSuspendFormatException(this.status);
        }
        this.registerEvent(new ControlFormatSuspended(this.id.value(), this.updatedAt));
    }

    public void resume() {
        try {
            this.requireSource(ControlFormatStatus.SUSPENDED);
            this.transitionTo(ControlFormatStatus.ACTIVE);
        } catch (InvalidFormatTransitionException e) {
            throw new CannotResumeFormatException(this.status);
        }
        this.registerEvent(new ControlFormatResumed(this.id.value(), this.updatedAt));
    }

    public void cease() {
        try {
            this.transitionTo(ControlFormatStatus.CEASED);
        } catch (InvalidFormatTransitionException e) {
            throw new CannotCeaseFormatException(this.status);
        }
        this.registerEvent(new ControlFormatCeased(this.id.value(), this.updatedAt));
    }

    /**
     * Replaces the whole field collection with the desired state ({@code PUT} semantics).
     * Reconciles against the currently held fields: an {@link FormatFieldDraft.Existing} draft
     * updates the matching field in place (throwing {@link FormatFieldNotFoundException} if its id
     * is unknown to this format), a {@link FormatFieldDraft.New} draft is created, and any current
     * field absent from the drafts is dropped. Editable only while DRAFT; keys must be unique
     * across the resulting set.
     */
    public void replaceFields(List<FormatFieldDraft> drafts) {
        this.requireEditable();
        Objects.requireNonNull(drafts, "drafts must not be null");

        Map<FormatFieldId, FormatField> current = new HashMap<>();
        for (FormatField field : this.fields) {
            current.put(field.getId(), field);
        }

        // Reserve the (frozen) keys of every surviving field first, so a newly slugged key never
        // collides with an existing one regardless of ordering. New keys are then derived unique.
        Set<String> usedKeys = new HashSet<>();
        for (FormatFieldDraft draft : drafts) {
            if (draft instanceof FormatFieldDraft.Existing e) {
                usedKeys.add(this.requireField(current, e.id()).getKey());
            }
        }

        List<FormatField> result = new ArrayList<>(drafts.size());
        for (FormatFieldDraft draft : drafts) {
            switch (draft) {
                case FormatFieldDraft.New n -> {
                    String key = uniqueKey(slugify(n.label()), usedKeys);
                    usedKeys.add(key);
                    result.add(FormatField.create(FormatFieldId.generate(), key, n.label(), n.type(),
                            n.required(), n.displayOrder(), n.validationRules()));
                }
                case FormatFieldDraft.Existing e -> {
                    FormatField existing = this.requireField(current, e.id());
                    existing.update(e.label(), e.type(), e.required(), e.displayOrder(), e.validationRules());
                    result.add(existing);
                }
            }
        }

        this.fields.clear();
        this.fields.addAll(result);
        this.touch();
    }

    private FormatField requireField(Map<FormatFieldId, FormatField> current, FormatFieldId id) {
        FormatField existing = current.get(id);
        if (existing == null) {
            throw FormatFieldNotFoundException.byId(id.value());
        }
        return existing;
    }

    /** Derives a stable, readable field key from its label: ASCII-folded, lowercased, non-alphanumeric runs collapsed to '_'. */
    private static String slugify(String label) {
        String ascii = Normalizer.normalize(label, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        String slug = ascii.toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
        return slug.isBlank() ? "field" : slug;
    }

    private static String uniqueKey(String base, Set<String> used) {
        if (!used.contains(base)) {
            return base;
        }
        int suffix = 2;
        while (used.contains(base + "_" + suffix)) {
            suffix++;
        }
        return base + "_" + suffix;
    }

    private void addField(FormatField field) {
        this.requireEditable();
        Objects.requireNonNull(field, "field must not be null");
        this.fields.add(field);
        this.touch();
    }

    private void transitionTo(ControlFormatStatus target) {
        if (!this.status.canTransitionTo(target)) {
            throw new InvalidFormatTransitionException();
        }
        this.status = target;
        this.touch();
    }

    /**
     * Guards a transition whose target alone is ambiguous (e.g. {@code ACTIVE} is reachable
     * from both {@code DRAFT} via activate and {@code SUSPENDED} via resume): pins the
     * required source state so each operation only applies from where it semantically should.
     * Throws the base signal; the calling operation rethrows its specific subtype.
     */
    private void requireSource(ControlFormatStatus expected) {
        if (this.status != expected) {
            throw new InvalidFormatTransitionException();
        }
    }

    private void requireEditable() {
        if (this.status != ControlFormatStatus.DRAFT) {
            throw new FormatNotEditableException(this.id.value(), this.status);
        }
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        return name.trim();
    }

    @Override
    public ControlFormatId getId() { return this.id; }
    public ControlProcessId getProcessId() { return this.processId; }
    public String getName() { return this.name; }
    public ControlFormatStatus getStatus() { return this.status; }
    public List<FormatField> getFields() { return Collections.unmodifiableList(this.fields); }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }
}
