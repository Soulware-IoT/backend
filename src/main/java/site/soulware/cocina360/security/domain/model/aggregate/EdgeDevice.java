package site.soulware.cocina360.security.domain.model.aggregate;

import site.soulware.cocina360.security.domain.model.event.EdgeDeviceApiKeyRotated;
import site.soulware.cocina360.security.domain.model.event.EdgeDeviceDeactivated;
import site.soulware.cocina360.security.domain.model.event.EdgeDeviceProvisioned;
import site.soulware.cocina360.security.domain.model.event.EdgeDeviceRegistered;
import site.soulware.cocina360.security.domain.model.exception.EdgeDeviceAlreadyClaimedException;
import site.soulware.cocina360.security.domain.model.exception.EdgeDeviceNotClaimedException;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceCode;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceStatus;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.time.Instant;

/**
 * The backend's acknowledgement of the physical edge device that fronts an
 * organization's IoT devices (1:1 with an organization), modelled with a two-phase
 * lifecycle that mirrors {@link IoTDevice}:
 * <ol>
 *   <li><b>Provisioned</b> at the factory step — the backend mints its {@link EdgeDeviceCode}
 *       and {@link ApiKey} (burned into the edge's configuration), with no organization yet.</li>
 *   <li><b>Claimed</b> by an organization — assigned org + name and put in service.</li>
 * </ol>
 * It is the {@code edge → backend} trust anchor: the edge presents its apiKey to identify
 * itself and pull its organization's device registry and thresholds.
 */
public class EdgeDevice extends AggregateRoot<EdgeDeviceId> {

    private final EdgeDeviceId id;
    private final EdgeDeviceCode code;
    private ApiKey apiKey;
    private OrganizationId organizationId;
    private String name;
    private EdgeDeviceStatus status;
    private final Instant createdAt;
    private ProfileId createdBy;
    private Instant updatedAt;
    private ProfileId updatedBy;

    private EdgeDevice(
        EdgeDeviceId id,
        EdgeDeviceCode code,
        ApiKey apiKey,
        OrganizationId organizationId,
        String name,
        EdgeDeviceStatus status,
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
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    /**
     * Factory step: mint an unassigned edge device with a generated code + apiKey. No
     * organization and no audited requester (provisioning is a system/admin action).
     */
    public static EdgeDevice provision(EdgeDeviceId id, EdgeDeviceCode code, ApiKey apiKey) {
        Instant now = Instant.now();
        EdgeDevice edgeDevice = new EdgeDevice(id, code, apiKey, null, null,
                EdgeDeviceStatus.PROVISIONED, now, null, now, null);
        edgeDevice.registerEvent(new EdgeDeviceProvisioned(id.value(), code.value(), now));
        return edgeDevice;
    }

    /**
     * Claim this provisioned edge device into an organization, assigning its name and
     * putting it in service. The claimer is recorded as creator.
     *
     * @throws EdgeDeviceAlreadyClaimedException if the edge device is not {@code PROVISIONED}.
     */
    public void claim(OrganizationId organizationId, String name, ProfileId requesterId) {
        if (this.status != EdgeDeviceStatus.PROVISIONED) {
            throw new EdgeDeviceAlreadyClaimedException(this.code.value());
        }
        this.organizationId = organizationId;
        this.name = name;
        this.status = EdgeDeviceStatus.ACTIVE;
        this.createdBy = requesterId;
        this.touch(requesterId);
        this.registerEvent(new EdgeDeviceRegistered(
                this.id.value(), organizationId.value(), this.updatedAt));
    }

    public static EdgeDevice rehydrate(
        EdgeDeviceId id,
        EdgeDeviceCode code,
        ApiKey apiKey,
        OrganizationId organizationId,
        String name,
        EdgeDeviceStatus status,
        Instant createdAt,
        ProfileId createdBy,
        Instant updatedAt,
        ProfileId updatedBy
    ) {
        return new EdgeDevice(id, code, apiKey, organizationId, name, status,
                createdAt, createdBy, updatedAt, updatedBy);
    }

    /**
     * Rename a claimed edge device.
     *
     * @throws EdgeDeviceNotClaimedException if the edge device is still {@code PROVISIONED}.
     */
    public void rename(String name, ProfileId updatedBy) {
        this.requireClaimed();
        this.name = name;
        this.touch(updatedBy);
    }

    /**
     * Put a claimed edge device back in service.
     *
     * @throws EdgeDeviceNotClaimedException if the edge device is still {@code PROVISIONED}.
     */
    public void activate(ProfileId updatedBy) {
        this.requireClaimed();
        this.status = EdgeDeviceStatus.ACTIVE;
        this.touch(updatedBy);
    }

    /**
     * Take a claimed edge device out of service; the backend stops trusting its key.
     *
     * @throws EdgeDeviceNotClaimedException if the edge device is still {@code PROVISIONED}.
     */
    public void deactivate(ProfileId updatedBy) {
        this.requireClaimed();
        this.status = EdgeDeviceStatus.INACTIVE;
        this.touch(updatedBy);
        this.registerEvent(new EdgeDeviceDeactivated(this.id.value(), this.updatedAt));
    }

    public void rotateApiKey(ProfileId updatedBy) {
        this.requireClaimed();
        this.apiKey = ApiKey.generate();
        this.touch(updatedBy);
        this.registerEvent(new EdgeDeviceApiKeyRotated(this.id.value(), this.updatedAt));
    }

    /** Management operations are only valid once an edge device has been claimed by an org. */
    private void requireClaimed() {
        if (this.status == EdgeDeviceStatus.PROVISIONED) {
            throw new EdgeDeviceNotClaimedException(this.code.value());
        }
    }

    private void touch(ProfileId updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }

    @Override
    public EdgeDeviceId getId() { return this.id; }
    public EdgeDeviceCode getCode() { return this.code; }
    public ApiKey getApiKey() { return this.apiKey; }
    public OrganizationId getOrganizationId() { return this.organizationId; }
    public String getName() { return this.name; }
    public EdgeDeviceStatus getStatus() { return this.status; }
    public Instant getCreatedAt() { return this.createdAt; }
    public ProfileId getCreatedBy() { return this.createdBy; }
    public Instant getUpdatedAt() { return this.updatedAt; }
    public ProfileId getUpdatedBy() { return this.updatedBy; }
}
