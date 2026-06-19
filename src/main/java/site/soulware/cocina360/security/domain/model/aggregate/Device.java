package site.soulware.cocina360.security.domain.model.aggregate;

import site.soulware.cocina360.security.domain.model.event.DeviceApiKeyRotated;
import site.soulware.cocina360.security.domain.model.event.DeviceDeactivated;
import site.soulware.cocina360.security.domain.model.event.DeviceRegistered;
import site.soulware.cocina360.security.domain.model.event.DeviceThresholdsUpdated;
import site.soulware.cocina360.security.domain.model.valueobject.ActivationStatus;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

import java.time.Instant;

/**
 * A physical IoT safety unit (ESP32 + sensors/actuators) registered to an
 * organization. The backend owns its identity, its safety {@link SafetyThresholds}
 * (the config the edge pulls and serves), and its {@link ApiKey} (the
 * {@code device → edge} credential the edge replicates to authenticate the device).
 */
public class Device extends AggregateRoot<DeviceId> {

    private final DeviceId id;
    private final OrganizationId organizationId;
    private final DeviceCode code;
    private String name;
    private ActivationStatus status;
    private ApiKey apiKey;
    private SafetyThresholds thresholds;
    private final Instant createdAt;
    private Instant updatedAt;

    private Device(
        DeviceId id,
        OrganizationId organizationId,
        DeviceCode code,
        String name,
        ActivationStatus status,
        ApiKey apiKey,
        SafetyThresholds thresholds,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.code = code;
        this.name = name;
        this.status = status;
        this.apiKey = apiKey;
        this.thresholds = thresholds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Device register(
        DeviceId id,
        OrganizationId organizationId,
        DeviceCode code,
        String name,
        SafetyThresholds thresholds
    ) {
        Instant now = Instant.now();
        Device device = new Device(id, organizationId, code, name, ActivationStatus.ACTIVE,
                ApiKey.generate(), thresholds, now, now);
        device.registerEvent(new DeviceRegistered(id.value(), organizationId.value(), code.value(), now));
        return device;
    }

    public static Device rehydrate(
        DeviceId id,
        OrganizationId organizationId,
        DeviceCode code,
        String name,
        ActivationStatus status,
        ApiKey apiKey,
        SafetyThresholds thresholds,
        Instant createdAt,
        Instant updatedAt
    ) {
        return new Device(id, organizationId, code, name, status, apiKey, thresholds, createdAt, updatedAt);
    }

    public void rename(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public void updateThresholds(SafetyThresholds thresholds) {
        this.thresholds = thresholds;
        this.updatedAt = Instant.now();
        this.registerEvent(new DeviceThresholdsUpdated(this.id.value(), this.updatedAt));
    }

    public void activate() {
        this.status = ActivationStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = ActivationStatus.INACTIVE;
        this.updatedAt = Instant.now();
        this.registerEvent(new DeviceDeactivated(this.id.value(), this.updatedAt));
    }

    public void rotateApiKey() {
        this.apiKey = ApiKey.generate();
        this.updatedAt = Instant.now();
        this.registerEvent(new DeviceApiKeyRotated(this.id.value(), this.updatedAt));
    }

    @Override
    public DeviceId getId() { return this.id; }
    public OrganizationId getOrganizationId() { return this.organizationId; }
    public DeviceCode getCode() { return this.code; }
    public String getName() { return this.name; }
    public ActivationStatus getStatus() { return this.status; }
    public ApiKey getApiKey() { return this.apiKey; }
    public SafetyThresholds getThresholds() { return this.thresholds; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }
}
