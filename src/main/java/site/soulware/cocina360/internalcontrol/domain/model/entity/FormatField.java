package site.soulware.cocina360.internalcontrol.domain.model.entity;

import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FieldType;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FormatFieldId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ValidationRules;
import site.soulware.cocina360.shared.domain.model.aggregate.Entity;

import java.util.Objects;

public class FormatField extends Entity<FormatFieldId> {

    private final FormatFieldId id;
    private String key;
    private String label;
    private FieldType type;
    private boolean required;
    private int displayOrder;
    private ValidationRules validationRules;

    private FormatField(FormatFieldId id, String key, String label, FieldType type,
                        boolean required, int displayOrder, ValidationRules validationRules) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.key = requireKey(key);
        this.label = requireLabel(label);
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.required = required;
        this.displayOrder = displayOrder;
        this.validationRules = Objects.requireNonNull(validationRules, "validationRules must not be null");
    }

    public static FormatField create(
        FormatFieldId id,
        String key,
        String label,
        FieldType type,
        boolean required,
        int displayOrder,
        ValidationRules validationRules
    ) {
        return new FormatField(id, key, label, type, required, displayOrder, validationRules);
    }

    public static FormatField rehydrate(
        FormatFieldId id,
        String key,
        String label,
        FieldType type,
        boolean required,
        int displayOrder,
        ValidationRules validationRules
    ) {
        return new FormatField(id, key, label, type, required, displayOrder, validationRules);
    }

    public void update(String label, FieldType type, boolean required, int displayOrder, ValidationRules validationRules) {
        this.label = requireLabel(label);
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.required = required;
        this.displayOrder = displayOrder;
        this.validationRules = Objects.requireNonNull(validationRules, "validationRules must not be null");
    }

    private static String requireKey(String key) {
        if (key == null || key.isBlank()) throw new IllegalArgumentException("key must not be blank");
        return key.trim().toLowerCase();
    }

    private static String requireLabel(String label) {
        if (label == null || label.isBlank()) throw new IllegalArgumentException("label must not be blank");
        return label.trim();
    }

    @Override
    public FormatFieldId getId() { return this.id; }
    public String getKey() { return this.key; }
    public String getLabel() { return this.label; }
    public FieldType getType() { return this.type; }
    public boolean isRequired() { return this.required; }
    public int getDisplayOrder() { return this.displayOrder; }
    public ValidationRules getValidationRules() { return this.validationRules; }
}
