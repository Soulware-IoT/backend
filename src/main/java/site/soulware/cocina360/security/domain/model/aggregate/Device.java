package site.soulware.cocina360.security.domain.model.aggregate;

import site.soulware.cocina360.security.domain.model.event.DeviceDeactivated;
import site.soulware.cocina360.security.domain.model.event.DeviceProvisioned;
import site.soulware.cocina360.security.domain.model.event.DeviceRegistered;
import site.soulware.cocina360.security.domain.model.event.DeviceThresholdsUpdated;
import site.soulware.cocina360.security.domain.model.exception.DeviceAlreadyClaimedException;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceStatus;
import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.time.Instant;

/**
 * A physical IoT safety unit (ESP32 + sensors/actuators), modelled with a two-phase
 * lifecycle:
 * <ol>
 *   <li><b>Provisioned</b> at the factory step — the backend mints its {@link DeviceCode}
 *       and {@link ApiKey} (burned into the firmware), with no organization yet.</li>
 *   <li><b>Claimed</b> by an organization — assigned org + name + {@link SafetyThresholds}
 *       and put in service.</li>
 * </ol>
 * The backend owns its identity, thresholds (the config the edge pulls), and apiKey
 * (the {@code device → edge} credential the edge replicates to authenticate the device).
 */
public class Device extends AggregateRoot<DeviceId> {

    private final DeviceId id;
    private final DeviceCode code;
    private final ApiKey apiKey;
    private OrganizationId organizationId;
    private String name;
    private DeviceStatus status;
    private SafetyThresholds thresholds;
    private final Instant createdAt;
    private ProfileId createdBy;
    private Instant updatedAt;
    private ProfileId updatedBy;

    private Device(
        DeviceId id,
        DeviceCode code,
        ApiKey apiKey,
        OrganizationId organizationId,
        String name,
        DeviceStatus status,
        SafetyThresholds thresholds,
        Instant createdAt,
        ProfileId createdBy,
        Instant updatedAt,
        ProfileId updatedBy
    ) {
        this.id = id;
        this.code = code;
        this.apiKey = apiKey;
        this.organizationId = organizationId;
        this.name = name;
        this.status = status;
        this.thresholds = thresholds;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    /**
     * Factory step: mint an unassigned device with a generated code + apiKey. No
     * organization and no audited requester (provisioning is a system/admin action);
     * thresholds start at the device's hardcoded defaults until it is claimed.
     */
    public static Device provision(DeviceId id, DeviceCode code, ApiKey apiKey) {
        Instant now = Instant.now();
        Device device = new Device(id, code, apiKey, null, null, DeviceStatus.PROVISIONED,
                SafetyThresholds.defaults(), now, null, now, null);
        device.registerEvent(new DeviceProvisioned(id.value(), code.value(), now));
        return device;
    }

    /**
     * Claim this provisioned device into an organization, assigning its name and
     * thresholds and putting it in service. The claimer is recorded as creator.
     *
     * @throws DeviceAlreadyClaimedException if the device is not {@code PROVISIONED}.
     */
    public void claim(
        OrganizationId organizationId,
        String name,
        SafetyThresholds thresholds,
        ProfileId requesterId
    ) {
        if (this.status != DeviceStatus.PROVISIONED) {
            throw new DeviceAlreadyClaimedException(this.code.value());
        }
        this.organizationId = organizationId;
        this.name = name;
        this.thresholds = thresholds;
        this.status = DeviceStatus.ACTIVE;
        this.createdBy = requesterId;
        this.touch(requesterId);
        this.registerEvent(new DeviceRegistered(
                this.id.value(), organizationId.value(), this.code.value(), this.updatedAt));
    }

    public static Device rehydrate(
        DeviceId id,
        DeviceCode code,
        ApiKey apiKey,
        OrganizationId organizationId,
        String name,
        DeviceStatus status,
        SafetyThresholds thresholds,
        Instant createdAt,
        ProfileId createdBy,
        Instant updatedAt,
        ProfileId updatedBy
    ) {
        return new Device(id, code, apiKey, organizationId, name, status, thresholds,
                createdAt, createdBy, updatedAt, updatedBy);
    }

    public void rename(String name, ProfileId updatedBy) {
        this.name = name;
        this.touch(updatedBy);
    }

    public void updateThresholds(SafetyThresholds thresholds, ProfileId updatedBy) {
        this.thresholds = thresholds;
        this.touch(updatedBy);
        this.registerEvent(new DeviceThresholdsUpdated(this.id.value(), this.updatedAt));
    }

    public void activate(ProfileId updatedBy) {
        this.status = DeviceStatus.ACTIVE;
        this.touch(updatedBy);
    }

    public void deactivate(ProfileId updatedBy) {
        this.status = DeviceStatus.INACTIVE;
        this.touch(updatedBy);
        this.registerEvent(new DeviceDeactivated(this.id.value(), this.updatedAt));
    }

    private void touch(ProfileId updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }

    @Override
    public DeviceId getId() { return this.id; }
    public DeviceCode getCode() { return this.code; }
    public ApiKey getApiKey() { return this.apiKey; }
    public OrganizationId getOrganizationId() { return this.organizationId; }
    public String getName() { return this.name; }
    public DeviceStatus getStatus() { return this.status; }
    public SafetyThresholds getThresholds() { return this.thresholds; }
    public Instant getCreatedAt() { return this.createdAt; }
    public ProfileId getCreatedBy() { return this.createdBy; }
    public Instant getUpdatedAt() { return this.updatedAt; }
    public ProfileId getUpdatedBy() { return this.updatedBy; }
}
