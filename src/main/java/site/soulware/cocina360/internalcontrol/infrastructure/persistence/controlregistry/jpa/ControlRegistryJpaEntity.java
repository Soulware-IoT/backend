package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlregistry.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;

import java.time.Instant;
import java.util.UUID;

/**
 * Maps the {@code control_registries} table. A registry is an <b>immutable</b> captured record:
 * once inserted it is never updated, so every column is {@code updatable = false}. The submitted
 * field values are stored as a single {@code jsonb} document ({@code data}) — its shape is dictated
 * at write time by the owning format's fields, so it has no fixed relational schema.
 */
@Entity
@Table(name = "control_registries", schema = "public")
public class ControlRegistryJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "format_id", nullable = false, updatable = false)
    private UUID formatId;

    @Column(name = "data", nullable = false, updatable = false)
    @ColumnTransformer(write = "?::jsonb")
    private String data;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ControlRegistryJpaEntity() {}

    public ControlRegistryJpaEntity(
        UUID id,
        UUID formatId,
        String data,
        Instant createdAt
    ) {
        this.id = id;
        this.formatId = formatId;
        this.data = data;
        this.createdAt = createdAt;
    }

    public UUID getId() { return this.id; }
    public UUID getFormatId() { return this.formatId; }
    public String getData() { return this.data; }
    public Instant getCreatedAt() { return this.createdAt; }
}
