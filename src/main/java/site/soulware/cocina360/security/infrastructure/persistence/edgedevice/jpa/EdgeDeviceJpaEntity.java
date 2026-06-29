package site.soulware.cocina360.security.infrastructure.persistence.edgedevice.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "edge_devices")
public class EdgeDeviceJpaEntity {

    @Id
    private UUID id;

    // Null while PROVISIONED (factory step); set when the edge device is claimed by an org.
    @Column(name = "organization_id", unique = true)
    private UUID organizationId;

    @Column(nullable = false, unique = true)
    private String code;

    // Null while PROVISIONED; set at claim.
    @Column
    private String name;

    // Mapped as varchar via EdgeDeviceStatusConverter. If the column is later promoted to
    // a PostgreSQL native enum, add @ColumnTransformer(write = "?::edge_device_status").
    @Column(nullable = false)
    private EdgeDeviceStatus status;

    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column
    private String ip;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Null while PROVISIONED; set to the claimer at claim time.
    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Null while PROVISIONED; set at claim.
    @Column(name = "updated_by")
    private UUID updatedBy;

    protected EdgeDeviceJpaEntity() {}

    public EdgeDeviceJpaEntity(
        UUID id,
        UUID organizationId,
        String code,
        String name,
        EdgeDeviceStatus status,
        String apiKey,
        String ip,
        Instant createdAt,
        UUID createdBy,
        Instant updatedAt,
        UUID updatedBy
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.code = code;
        this.name = name;
        this.status = status;
        this.apiKey = apiKey;
        this.ip = ip;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public UUID getId() { return this.id; }
    public UUID getOrganizationId() { return this.organizationId; }
    public String getCode() { return this.code; }
    public String getName() { return this.name; }
    public EdgeDeviceStatus getStatus() { return this.status; }
    public String getApiKey() { return this.apiKey; }
    public String getIp() { return this.ip; }
    public Instant getCreatedAt() { return this.createdAt; }
    public UUID getCreatedBy() { return this.createdBy; }
    public Instant getUpdatedAt() { return this.updatedAt; }
    public UUID getUpdatedBy() { return this.updatedBy; }
}
