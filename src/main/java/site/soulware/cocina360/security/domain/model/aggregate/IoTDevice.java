package site.soulware.cocina360.security.domain.model.aggregate;

import site.soulware.cocina360.security.domain.model.event.IoTDeviceDeactivated;
import site.soulware.cocina360.security.domain.model.event.IoTDeviceProvisioned;
import site.soulware.cocina360.security.domain.model.event.IoTDeviceRegistered;
import site.soulware.cocina360.security.domain.model.event.IoTDeviceThresholdsUpdated;
import site.soulware.cocina360.security.domain.model.exception.IoTDeviceAlreadyClaimedException;
import site.soulware.cocina360.security.domain.model.exception.IoTDeviceNotClaimedException;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceStatus;
import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.time.Instant;

/**
 * A physical IoT safety unit (ESP32 + sensors/actuators), modelled with a two-phase
 * lifecycle:
 * <ol>
 *   <li><b>Provisioned</b> at the factory step — the backend mints its {@link IoTDeviceCode}
 *       and {@link ApiKey} (burned into the firmware), with no organization yet.</li>
 *   <li><b>Claimed</b> by an organization — assigned org + name + {@link SafetyThresholds}
 *       and put in service.</li>
 * </ol>
 * The backend owns its identity, thresholds (the config the edge pulls), and apiKey
 * (the {@code device → edge} credential the edge replicates to authenticate the device).
 */
public class IoTDevice extends AggregateRoot<IoTDeviceId> {

    private final IoTDeviceId id;
    private final IoTDeviceCode code;
    private final ApiKey apiKey;
    private OrganizationId organizationId;
    private String name;
    private IoTDeviceStatus status;
    private SafetyThresholds thresholds;
    private final Instant createdAt;
    private ProfileId createdBy;
    private Instant updatedAt;
    private ProfileId updatedBy;

    private IoTDevice(
        IoTDeviceId id,
        IoTDeviceCode code,
        ApiKey apiKey,
        OrganizationId organizationId,
        String name,
        IoTDeviceStatus status,
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
    public static IoTDevice provision(IoTDeviceId id, IoTDeviceCode code, ApiKey apiKey) {
        Instant now = Instant.now();
        IoTDevice device = new IoTDevice(id, code, apiKey, null, null, IoTDeviceStatus.PROVISIONED,
                SafetyThresholds.defaults(), now, null, now, null);
        device.registerEvent(new IoTDeviceProvisioned(id.value(), code.value(), now));
        return device;
    }

    /**
     * Claim this provisioned device into an organization, assigning its name and
     * thresholds and putting it in service. The claimer is recorded as creator.
     *
     * @throws IoTDeviceAlreadyClaimedException if the device is not {@code PROVISIONED}.
     */
    public void claim(
        OrganizationId organizationId,
        String name,
        SafetyThresholds thresholds,
        ProfileId requesterId
    ) {
        if (this.status != IoTDeviceStatus.PROVISIONED) {
            throw new IoTDeviceAlreadyClaimedException(this.code.value());
        }
        this.organizationId = organizationId;
        this.name = name;
        this.thresholds = thresholds;
        this.status = IoTDeviceStatus.ACTIVE;
        this.createdBy = requesterId;
        this.touch(requesterId);
        this.registerEvent(new IoTDeviceRegistered(
                this.id.value(), organizationId.value(), this.code.value(), this.updatedAt));
    }

    public static IoTDevice rehydrate(
        IoTDeviceId id,
        IoTDeviceCode code,
        ApiKey apiKey,
        OrganizationId organizationId,
        String name,
        IoTDeviceStatus status,
        SafetyThresholds thresholds,
        Instant createdAt,
        ProfileId createdBy,
        Instant updatedAt,
        ProfileId updatedBy
    ) {
        return new IoTDevice(id, code, apiKey, organizationId, name, status, thresholds,
                createdAt, createdBy, updatedAt, updatedBy);
    }

    /**
     * Rename a claimed device.
     *
     * @throws IoTDeviceNotClaimedException if the device is still {@code PROVISIONED}.
     */
    public void rename(String name, ProfileId updatedBy) {
        this.requireClaimed();
        this.name = name;
        this.touch(updatedBy);
    }

    /**
     * Recalibrate the device's safety thresholds.
     *
     * @throws IoTDeviceNotClaimedException if the device is still {@code PROVISIONED} —
     *         factory configuration cannot be changed before it is claimed by an org.
     */
    public void updateThresholds(SafetyThresholds thresholds, ProfileId updatedBy) {
        this.requireClaimed();
        this.thresholds = thresholds;
        this.touch(updatedBy);
        this.registerEvent(new IoTDeviceThresholdsUpdated(this.id.value(), this.updatedAt));
    }

    /**
     * Put a claimed device back in service.
     *
     * @throws IoTDeviceNotClaimedException if the device is still {@code PROVISIONED}.
     */
    public void activate(ProfileId updatedBy) {
        this.requireClaimed();
        this.status = IoTDeviceStatus.ACTIVE;
        this.touch(updatedBy);
    }

    /**
     * Take a claimed device out of service; the edge stops trusting it.
     *
     * @throws IoTDeviceNotClaimedException if the device is still {@code PROVISIONED}.
     */
    public void deactivate(ProfileId updatedBy) {
        this.requireClaimed();
        this.status = IoTDeviceStatus.INACTIVE;
        this.touch(updatedBy);
        this.registerEvent(new IoTDeviceDeactivated(this.id.value(), this.updatedAt));
    }

    /** Management operations are only valid once a device has been claimed by an org. */
    private void requireClaimed() {
        if (this.status == IoTDeviceStatus.PROVISIONED) {
            throw new IoTDeviceNotClaimedException(this.code.value());
        }
    }

    private void touch(ProfileId updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }

    @Override
    public IoTDeviceId getId() { return this.id; }
    public IoTDeviceCode getCode() { return this.code; }
    public ApiKey getApiKey() { return this.apiKey; }
    public OrganizationId getOrganizationId() { return this.organizationId; }
    public String getName() { return this.name; }
    public IoTDeviceStatus getStatus() { return this.status; }
    public SafetyThresholds getThresholds() { return this.thresholds; }
    public Instant getCreatedAt() { return this.createdAt; }
    public ProfileId getCreatedBy() { return this.createdBy; }
    public Instant getUpdatedAt() { return this.updatedAt; }
    public ProfileId getUpdatedBy() { return this.updatedBy; }
}
