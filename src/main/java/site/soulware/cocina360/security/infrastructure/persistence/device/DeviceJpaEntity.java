package site.soulware.cocina360.security.infrastructure.persistence.device;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import site.soulware.cocina360.security.domain.model.valueobject.ActivationStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class DeviceJpaEntity {

    @Id
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    // Mapped as varchar via ActivationStatusConverter. If the column is later promoted to
    // a PostgreSQL native enum, add @ColumnTransformer(write = "?::activation_status").
    @Column(nullable = false)
    private ActivationStatus status;

    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column(name = "warn_temperature_c", nullable = false)
    private int warnTemperatureC;

    @Column(name = "crit_temperature_c", nullable = false)
    private int critTemperatureC;

    @Column(name = "warn_gas_ppm", nullable = false)
    private double warnGasPpm;

    @Column(name = "crit_gas_ppm", nullable = false)
    private double critGasPpm;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private UUID createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by", nullable = false)
    private UUID updatedBy;

    protected DeviceJpaEntity() {}

    public DeviceJpaEntity(
        UUID id,
        UUID organizationId,
        String code,
        String name,
        ActivationStatus status,
        String apiKey,
        int warnTemperatureC,
        int critTemperatureC,
        double warnGasPpm,
        double critGasPpm,
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
        this.warnTemperatureC = warnTemperatureC;
        this.critTemperatureC = critTemperatureC;
        this.warnGasPpm = warnGasPpm;
        this.critGasPpm = critGasPpm;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public UUID getId() { return this.id; }
    public UUID getOrganizationId() { return this.organizationId; }
    public String getCode() { return this.code; }
    public String getName() { return this.name; }
    public ActivationStatus getStatus() { return this.status; }
    public String getApiKey() { return this.apiKey; }
    public int getWarnTemperatureC() { return this.warnTemperatureC; }
    public int getCritTemperatureC() { return this.critTemperatureC; }
    public double getWarnGasPpm() { return this.warnGasPpm; }
    public double getCritGasPpm() { return this.critGasPpm; }
    public Instant getCreatedAt() { return this.createdAt; }
    public UUID getCreatedBy() { return this.createdBy; }
    public Instant getUpdatedAt() { return this.updatedAt; }
    public UUID getUpdatedBy() { return this.updatedBy; }
}
