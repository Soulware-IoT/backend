package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "control_formats", schema = "public")
public class ControlFormatJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "process_id", nullable = false, updatable = false)
    private UUID processId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    @ColumnTransformer(write = "?::control_format_status")
    private ControlFormatStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ControlFormatJpaEntity() {}

    public ControlFormatJpaEntity(
        UUID id,
        UUID processId,
        String name,
        ControlFormatStatus status,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = id;
        this.processId = processId;
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return this.id; }
    public UUID getProcessId() { return this.processId; }
    public String getName() { return this.name; }
    public ControlFormatStatus getStatus() { return this.status; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }
}
