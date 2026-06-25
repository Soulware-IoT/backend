package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FieldType;

import java.util.UUID;

@Entity
@Table(name = "control_format_fields", schema = "public")
public class ControlFormatFieldJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "format_id", nullable = false, updatable = false)
    private UUID formatId;

    @Column(name = "key", nullable = false, updatable = false)
    private String key;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "type", nullable = false, updatable = false)
    @ColumnTransformer(write = "?::field_type")
    private FieldType type;

    @Column(name = "required", nullable = false)
    private boolean required;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "validation_rules", nullable = false)
    @ColumnTransformer(write = "?::jsonb")
    private String validationRules;

    protected ControlFormatFieldJpaEntity() {}

    public ControlFormatFieldJpaEntity(
        UUID id,
        UUID formatId,
        String key,
        String label,
        FieldType type,
        boolean required,
        int displayOrder,
        String validationRules
    ) {
        this.id = id;
        this.formatId = formatId;
        this.key = key;
        this.label = label;
        this.type = type;
        this.required = required;
        this.displayOrder = displayOrder;
        this.validationRules = validationRules;
    }

    public UUID getId() { return this.id; }
    public UUID getFormatId() { return this.formatId; }
    public String getKey() { return this.key; }
    public String getLabel() { return this.label; }
    public FieldType getType() { return this.type; }
    public boolean isRequired() { return this.required; }
    public int getDisplayOrder() { return this.displayOrder; }
    public String getValidationRules() { return this.validationRules; }
}
